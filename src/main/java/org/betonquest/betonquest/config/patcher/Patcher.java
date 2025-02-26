package org.betonquest.betonquest.config.patcher;

import org.betonquest.betonquest.api.config.patcher.PatchException;
import org.betonquest.betonquest.api.config.patcher.PatchTransformer;
import org.betonquest.betonquest.api.config.patcher.PatchTransformerRegistry;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.versioning.UpdateStrategy;
import org.betonquest.betonquest.versioning.Version;
import org.betonquest.betonquest.versioning.VersionComparator;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Patches BetonQuest's configuration file.
 */
public class Patcher {
    /**
     * The comment at the version entry in the config.
     */
    private static final String VERSION_CONFIG_COMMENT = "Don't change this! The plugin's automatic config updater handles it.";

    /**
     * The path to the config's version in the config.
     */
    private static final String CONFIG_VERSION_PATH = "configVersion";

    /**
     * Default version that is used when no configVersion is set.
     */
    private static final Version TECHNICAL_DEFAULT_VERSION = new Version("0.0.0-CONFIG-0");

    /**
     * Regex pattern of the internal config version schema.
     */
    private static final Pattern VERSION_PATTERN = Pattern.compile("(\\d*\\.\\d*\\.\\d*)\\.(\\d*)");

    /**
     * Comparator for {@link Version} with the qualifier CONFIG.
     */
    private static final VersionComparator VERSION_COMPARATOR = new VersionComparator(UpdateStrategy.MAJOR, "CONFIG-");

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * Registry for all {@link PatchTransformer}s.
     */
    private final PatchTransformerRegistry transformerRegistry;

    /**
     * Contains all versions that are patchable.
     */
    private final NavigableMap<Version, List<Map<?, ?>>> patches;

    /**
     * Creates a new Patcher.
     * <br>
     * Updates can be applied using {@link Patcher#patch(String, ConfigurationSection)}.
     *
     * @param log                 the logger that will be used for logging
     * @param transformerRegistry the registry for all {@link PatchTransformer}s
     * @param patchConfig         the patchConfig that contains patches
     * @throws InvalidConfigurationException if the patchConfig is malformed
     */
    public Patcher(final BetonQuestLogger log, final PatchTransformerRegistry transformerRegistry, final ConfigurationSection patchConfig) throws InvalidConfigurationException {
        this.log = log;
        this.transformerRegistry = transformerRegistry;
        this.patches = new TreeMap<>(VERSION_COMPARATOR);
        buildVersionIndex(patchConfig, "");
    }

    @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
    private void buildVersionIndex(final ConfigurationSection section, final String previousKeys) throws InvalidConfigurationException {
        for (final String key : section.getKeys(false)) {
            final String currentKey = previousKeys.isEmpty() ? key : previousKeys + "." + key;
            if (section.isConfigurationSection(key)) {
                final ConfigurationSection nestedSection = section.getConfigurationSection(key);
                if (nestedSection == null) {
                    throw new InvalidConfigurationException("The patch file at '" + currentKey + "' is not a list or a section.");
                }
                buildVersionIndex(nestedSection, currentKey);
            } else if (currentKey.split("\\.").length == 4) {
                collectVersion(currentKey, section.getMapList(key));
            } else {
                throw new InvalidConfigurationException("The patch file at '" + currentKey + "' is too long or too short.");
            }
        }
    }

    private void collectVersion(final String currentKey, @NotNull final List<Map<?, ?>> mapList) throws InvalidConfigurationException {
        final Matcher matcher = VERSION_PATTERN.matcher(currentKey);
        if (!matcher.matches()) {
            throw new InvalidConfigurationException("The patch file at '" + currentKey + "' has an invalid version format.");
        }
        final String result = matcher.group(1) + "-CONFIG-" + matcher.group(2);
        final Version discoveredVersion = new Version(result);
        patches.put(discoveredVersion, mapList);
    }

    /**
     * Patches the given config with the given patch file.
     *
     * @param configPath the path to the config file
     * @param config     the config to patch
     * @return whether changes were applied
     */
    public boolean patch(final String configPath, final ConfigurationSection config) {
        final Version version = getConfigVersion(config);
        if (!patches.isEmpty() && !VERSION_COMPARATOR.isOtherNewerOrEqualThanCurrent(version, patches.lastEntry().getKey())) {
            log.debug("The config file '" + configPath + "' is already up to date.");
            return false;
        }
        log.info("Updating config file '" + configPath + "' from version '" + version.getVersion() + "'...");
        boolean noErrors = true;
        boolean patched = false;
        for (final Map.Entry<Version, List<Map<?, ?>>> patch : patches.entrySet()) {
            if (!VERSION_COMPARATOR.isOtherNewerThanCurrent(version, patch.getKey())) {
                continue;
            }
            log.info("Applying patches to update to '" + patch.getKey().getVersion() + "'...");
            setConfigVersion(config, patch.getKey());
            if (!applyPatch(config, patch.getValue())) {
                noErrors = false;
            }
            patched = true;
        }
        if (noErrors) {
            log.info("Patching complete!");
        } else {
            log.warn("The patching progress did not go flawlessly. However, this does not mean your configs "
                    + "are now corrupted. Please check the errors above to see what the patcher did. "
                    + "You might want to adjust your config manually depending on that information.");
        }
        if (!patched) {
            if (patches.isEmpty()) {
                setConfigVersion(config, TECHNICAL_DEFAULT_VERSION);
            } else {
                setConfigVersion(config, patches.lastEntry().getKey());
            }
            return true;
        }
        return patched;
    }

    private Version getConfigVersion(final ConfigurationSection config) {
        final String configVersion = config.getString(CONFIG_VERSION_PATH);
        if (configVersion == null) {
            return TECHNICAL_DEFAULT_VERSION;
        } else if (configVersion.isEmpty()) {
            return patches.lastEntry().getKey();
        } else {
            return new Version(configVersion);
        }
    }

    private boolean applyPatch(final ConfigurationSection config, final List<Map<?, ?>> patchData) {
        boolean noErrors = true;
        for (final Map<?, ?> transformationData : patchData) {
            final Map<String, String> typeSafeTransformationData = transformationData.entrySet().stream()
                    .map(entry -> Map.entry(String.valueOf(entry.getKey()), String.valueOf(entry.getValue())))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            try {
                getPatchTransformer(typeSafeTransformationData.get("type")).transform(typeSafeTransformationData, config);
            } catch (final PatchException e) {
                noErrors = false;
                log.warn("There has been an issue while applying the patches: " + e.getMessage());
            }
        }
        return noErrors;
    }

    private PatchTransformer getPatchTransformer(@Nullable final String transformationType) throws PatchException {
        if (transformationType == null) {
            throw new PatchException("Missing transformation type for patcher!");
        }

        final String transformationTypeUpperCase = transformationType.toUpperCase(Locale.ROOT);
        final PatchTransformer patchTransformer = transformerRegistry.getTransformers().get(transformationTypeUpperCase);
        if (patchTransformer == null) {
            throw new PatchException("Unknown transformation type '" + transformationTypeUpperCase + "' used!");
        }
        return patchTransformer;
    }

    private void setConfigVersion(final ConfigurationSection config, final Version newVersion) {
        config.set(CONFIG_VERSION_PATH, newVersion.getVersion());
        config.setInlineComments(CONFIG_VERSION_PATH, List.of(VERSION_CONFIG_COMMENT));
    }
}
