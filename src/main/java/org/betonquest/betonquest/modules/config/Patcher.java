package org.betonquest.betonquest.modules.config;

import lombok.CustomLog;
import org.betonquest.betonquest.modules.config.transformer.KeyRenameTransformation;
import org.betonquest.betonquest.modules.config.transformer.ListEntryAddTransformation;
import org.betonquest.betonquest.modules.config.transformer.ListEntryRemoveTransformation;
import org.betonquest.betonquest.modules.config.transformer.ListEntryRenameTransformation;
import org.betonquest.betonquest.modules.config.transformer.PatchTransformation;
import org.betonquest.betonquest.modules.config.transformer.RemoveTransformation;
import org.betonquest.betonquest.modules.config.transformer.SetTransformation;
import org.betonquest.betonquest.modules.config.transformer.ValueRenameTransformation;
import org.betonquest.betonquest.modules.versioning.UpdateStrategy;
import org.betonquest.betonquest.modules.versioning.Version;
import org.betonquest.betonquest.modules.versioning.VersionComparator;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Patches BetonQuest's configuration file.
 */
@CustomLog
public class Patcher {

    /**
     * Regex pattern of the internal config version schema.
     */
    public static final Pattern VERSION_PATTERN = Pattern.compile("(\\d*\\.\\d*\\.\\d*)\\.(\\d*)");
    /**
     * The config to patch.
     */
    private final ConfigurationSection pluginConfig;
    /**
     * A config that contains one or more patches that will be applied to the pluginConfig.
     * <br>
     * A patch consists of one or multiple list entries of which each contains options for a {@link PatchTransformation}.
     * Additionally, each patch has a version that determines if the patch will be applied.
     */
    private final ConfigurationSection patchConfig;

    /**
     * Contains all versions that are newer then the config's current version.
     * A pair of patchable versions with the corresponding config path in the patch file.
     */
    private final Map<Version, String> patchableVersions = new TreeMap<>(new VersionComparator(UpdateStrategy.MAJOR, "CONFIG-"));
    /**
     * The {@link VersionComparator} that compares the versions of patches.
     */
    private final VersionComparator comparator = new VersionComparator(UpdateStrategy.MAJOR, "CONFIG-");
    /**
     * The current version of the plugin's config.
     */
    private final Version configVersion;

    /**
     * A map with the ID's and instances of all registered {@link PatchTransformation}s.
     */
    private final Map<String, PatchTransformation> transformers = new HashMap<>();

    /**
     * Creates a new Patcher.
     * <br>
     * Check for available patches using {@link Patcher#hasUpdate()}.
     * <br>
     * Updates can be applied using {@link Patcher#patch()}.
     *
     * @param config      the config that must be patched
     * @param patchConfig the patchConfig that contains patches
     */
    public Patcher(final ConfigurationSection config, final ConfigurationSection patchConfig) {
        this.pluginConfig = config;
        this.patchConfig = patchConfig;
        final String configVersion = config.getString("configVersion", "2.0.0-CONFIG-0");
        this.configVersion = new Version(configVersion);
        try {
            buildVersionIndex(this.patchConfig, "");
        } catch (final InvalidConfigurationException e) {
            LOG.error("Invalid patch file! " + e.getMessage(), e);
        }
        registerDefaultTransformers();
    }

    /**
     * Checks if the Patcher has a patch that is newer than the configs current version.
     *
     * @return if there is a patch newer than the config
     */
    public boolean hasUpdate() {
        return !patchableVersions.isEmpty();
    }

    @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
    private void buildVersionIndex(final ConfigurationSection section, final String previousKeys) throws InvalidConfigurationException {
        for (final String key : section.getKeys(false)) {
            final String currentKey = "".equals(previousKeys) ? key : previousKeys + "." + key;

            if (section.getList(key) == null) {
                final ConfigurationSection nestedSection = section.getConfigurationSection(key);
                if (nestedSection == null) {
                    throw new InvalidConfigurationException("The patch is malformed.");
                } else {
                    buildVersionIndex(nestedSection, currentKey);
                }
            } else if (currentKey.split("\\.").length == 4) {
                collectVersion(currentKey);
            } else {
                throw new InvalidConfigurationException("A version number is too short or too long.");
            }
        }
    }

    private void collectVersion(final String currentKey) {
        final Matcher matcher = VERSION_PATTERN.matcher(currentKey);
        if (matcher.matches()) {
            final String result = matcher.group(1) + "-CONFIG-" + matcher.group(2);
            final Version discoveredVersion = new Version(result);
            if (comparator.isOtherNewerThanCurrent(configVersion, discoveredVersion)) {
                patchableVersions.put(discoveredVersion, currentKey);
            }
        }
    }

    /**
     * Patches the given config with the given patch file.
     *
     * @return whether the patch could be applied successfully
     */
    public boolean patch() {
        boolean noErrors = true;
        for (final String key : patchableVersions.values()) {
            LOG.info("Applying patches to update to '" + key + "'...");
            pluginConfig.set("configVersion", getNewVersion(key));
            pluginConfig.setInlineComments("configVersion", List.of("Don't change this! The plugin's automatic config updater handles it."));
            if (!applyPatch(key)) {
                noErrors = false;
            }
        }
        return noErrors;
    }

    private String getNewVersion(final String key) {
        final int lastPoint = key.lastIndexOf('.');
        final String first = key.substring(0, lastPoint);
        final String second = key.substring(lastPoint + 1);
        return first + "-CONFIG-" + second;
    }

    /**
     * Applies the patches from the given patchDataPath.
     *
     * @param patchDataPath the path to the patches to apply
     * @return whether the patches were applied successfully
     */
    private boolean applyPatch(final String patchDataPath) {
        final var patchData = patchConfig.getMapList(patchDataPath);

        boolean noErrors = true;
        for (final Map<?, ?> transformationData : patchData) {
            final Map<String, String> typeSafeTransformationData = new HashMap<>();
            transformationData.forEach((key, value) -> typeSafeTransformationData.put(String.valueOf(key), String.valueOf(value)));

            final String transformationType = typeSafeTransformationData.get("type").toUpperCase(Locale.ROOT);
            try {
                applyTransformation(typeSafeTransformationData, transformationType);
            } catch (final PatchException e) {
                noErrors = false;
                LOG.warn("There has been an issue while applying the patches for '" + patchDataPath + "': " + e.getMessage());
            }
        }
        return noErrors;
    }

    private void applyTransformation(final Map<String, String> transformationData, final String transformationType) throws PatchException {
        if (!transformers.containsKey(transformationType)) {
            throw new PatchException("Unknown transformation type '" + transformationType + "' used!");
        }
        LOG.info("Applying patch of type '" + transformationType + "'...");
        transformers.get(transformationType).transform(transformationData, pluginConfig);
    }

    private void registerDefaultTransformers() {
        transformers.put("SET", new SetTransformation());
        transformers.put("REMOVE", new RemoveTransformation());
        transformers.put("KEY_RENAME", new KeyRenameTransformation());
        transformers.put("VALUE_RENAME", new ValueRenameTransformation());
        transformers.put("LIST_ENTRY_ADD", new ListEntryAddTransformation());
        transformers.put("LIST_ENTRY_REMOVE", new ListEntryRemoveTransformation());
        transformers.put("LIST_ENTRY_RENAME", new ListEntryRenameTransformation());
    }
}
