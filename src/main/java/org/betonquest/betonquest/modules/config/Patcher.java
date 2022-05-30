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
     * Each patch inside this config has a version that determines if the patch will be applied.
     */
    private final ConfigurationSection patchConfig;

    /**
     * A pair of versions with the corresponding config path from the patch.
     */
    private final Map<Version, String> versionIndex = new TreeMap<>(new VersionComparator(UpdateStrategy.MAJOR, "CONFIG-"));
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
        this.configVersion = new Version(config.getString("configVersion"));
        try {
            buildVersionIndex(this.patchConfig, "");
        } catch (final InvalidConfigurationException e) {
            LOG.error("Invalid patch file! A version number is too short.", e);
        }
        registerDefaultTransformers();
    }

    /**
     * Checks if the Patcher has a patch that is newer than the configs current version.
     *
     * @return if there is a patch newer than the config
     */
    public boolean hasUpdate() {
        return !versionIndex.isEmpty();
    }


    private void buildVersionIndex(final ConfigurationSection section, final String previousKeys) throws InvalidConfigurationException {
        for (final String key : section.getKeys(false)) {
            final String currentKey = "".equals(previousKeys) ? key : previousKeys + "." + key;

            if (section.getList(key) == null) {
                final ConfigurationSection nestedSection = section.getConfigurationSection(key);
                if (nestedSection == null) {
                    throw new InvalidConfigurationException("Invalid patch file! A version number is too short.");
                } else {
                    buildVersionIndex(nestedSection, currentKey);
                }
            } else {
                collectVersion(currentKey);
            }
        }
    }

    private void collectVersion(final String currentKey) {
        final Matcher matcher = VERSION_PATTERN.matcher(currentKey);
        if (matcher.matches()) {
            final String result = matcher.group(1) + "-CONFIG-" + matcher.group(2);
            final Version discoveredVersion = new Version(result);
            if (comparator.isOtherNewerThanCurrent(configVersion, discoveredVersion)) {
                versionIndex.put(discoveredVersion, currentKey);
            }
        }
    }

    /**
     * Patches the given config with the given patch file.
     *
     * @return whether the patch could be applied successfully
     */
    public boolean patch() {
        for (final String key : versionIndex.values()) {
            applyPatch(key);
            pluginConfig.set("configVersion", getNewVersion(key));
        }
        return true;
    }

    private String getNewVersion(final String key) {
        final int lastPoint = key.lastIndexOf('.');
        final String first = key.substring(0, lastPoint);
        final String second = key.substring(lastPoint + 1);
        return first + "-CONFIG-" + second;
    }

    private void applyPatch(final String patchDataPath) {
        final var patchData = patchConfig.getMapList(patchDataPath);
        patchData.forEach(transformationData -> {
            final Map<String, String> typeSafeTransformationData = new HashMap<>();
            transformationData.forEach((key, value) -> {
                typeSafeTransformationData.put(String.valueOf(key), String.valueOf(value));
            });

            final String transformationType = transformationData.get("type").toString().toUpperCase(Locale.ROOT);
            applyTransformation(typeSafeTransformationData, transformationType);
        });
    }

    private void applyTransformation(final Map<String, String> transformationData, final String transformationType) {
        for (final Map.Entry<String, PatchTransformation> transformer : transformers.entrySet()) {
            if (transformer.getKey().equals(transformationType)) {
                transformer.getValue().transform(transformationData, pluginConfig);
                return;
            }
        }
        LOG.warn("The patch file for a config contains invalid patch types!");
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
