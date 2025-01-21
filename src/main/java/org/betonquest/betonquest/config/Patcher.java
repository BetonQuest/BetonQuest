package org.betonquest.betonquest.config;

import org.betonquest.betonquest.api.config.patcher.PatchException;
import org.betonquest.betonquest.api.config.patcher.PatchTransformer;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.versioning.UpdateStrategy;
import org.betonquest.betonquest.versioning.Version;
import org.betonquest.betonquest.versioning.VersionComparator;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
     * Default version that is used for logging when no configVersion is set.
     */
    private static final String USER_DEFAULT_VERSION = "Legacy config";

    /**
     * Default version that is used when no configVersion is set.
     */
    private static final String TECHNICAL_DEFAULT_VERSION = "0.0.0-CONFIG-0";

    /**
     * Regex pattern of the internal config version schema.
     */
    private static final Pattern VERSION_PATTERN = Pattern.compile("(\\d*\\.\\d*\\.\\d*)\\.(\\d*)");

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * The config to patch.
     */
    private final ConfigurationSection pluginConfig;

    /**
     * A config that contains one or more patches that will be applied to the pluginConfig.
     * <br>
     * A patch consists of one or multiple list entries of which each contains options for a {@link PatchTransformer}.
     * Additionally, each patch has a version that determines if the patch will be applied.
     */
    private final ConfigurationSection patchConfig;

    /**
     * Contains all versions that are newer then the config's current version.
     * A pair of patchable versions with the corresponding config path in the patch file.
     */
    private final NavigableMap<Version, String> patchableVersions = new TreeMap<>(new VersionComparator(UpdateStrategy.MAJOR, "CONFIG-"));

    /**
     * The {@link VersionComparator} that compares the versions of patches.
     */
    private final VersionComparator comparator = new VersionComparator(UpdateStrategy.MAJOR, "CONFIG-");

    /**
     * The current version of the plugin's config.
     */
    private final Version configVersion;

    /**
     * A map with the ID's and instances of all registered {@link PatchTransformer}s.
     */
    private final Map<String, PatchTransformer> transformers = new HashMap<>();

    /**
     * Creates a new Patcher.
     * <br>
     * Check for available patches using {@link Patcher#hasUpdate()}.
     * <br>
     * Updates can be applied using {@link Patcher#patch()}.
     *
     * @param log         the logger that will be used for logging
     * @param config      the config that must be patched
     * @param patchConfig the patchConfig that contains patches
     */
    public Patcher(final BetonQuestLogger log, final ConfigurationSection config, final ConfigurationSection patchConfig) {
        this.log = log;
        this.pluginConfig = config;
        this.patchConfig = patchConfig;
        try {
            buildVersionIndex(this.patchConfig, "");
        } catch (final InvalidConfigurationException e) {
            this.log.error("Invalid patch file! " + e.getMessage(), e);
        }
        final String configVersion = config.getString(CONFIG_VERSION_PATH, TECHNICAL_DEFAULT_VERSION);
        if (configVersion.isEmpty()) {
            if (patchableVersions.isEmpty()) {
                this.configVersion = new Version(TECHNICAL_DEFAULT_VERSION);
            } else {
                final Map.Entry<Version, String> newestVersion = patchableVersions.lastEntry();
                this.configVersion = newestVersion.getKey();
            }
        } else {
            this.configVersion = new Version(configVersion);
        }
    }

    /**
     * Gets the highest available patch version.
     * This is the version that the config can be patched to if {@link Patcher#hasUpdate()} is true.
     *
     * @return the highest available patch version
     */
    public Version getNextConfigVersion() {
        return patchableVersions.lastEntry().getKey();
    }

    /**
     * Gets the version that the config is currently at.
     * Will return {@link Patcher#USER_DEFAULT_VERSION} if the currentVersion is {@link Patcher#TECHNICAL_DEFAULT_VERSION}.
     *
     * @return the version that the config is currently at
     */
    public String getCurrentConfigVersion() {
        if (TECHNICAL_DEFAULT_VERSION.equals(configVersion.getVersion())) {
            return USER_DEFAULT_VERSION;
        } else {
            return configVersion.getVersion();
        }
    }

    /**
     * Checks if the Patcher has a patch that is newer than the configs current version.
     *
     * @return if there is a patch newer than the config
     */
    public boolean hasUpdate() {
        return patchableVersions.keySet().stream()
                .anyMatch((patchVersion) -> comparator.isOtherNewerThanCurrent(configVersion, patchVersion));
    }

    /**
     * Updates the configVersion to the version of the newest available patch if it is an empty string.
     * This is useful to set the current config version when a default resource file is freshly copied to the plugin's folder.
     *
     * @return if the version was updated
     */
    public boolean updateVersion() {
        final String currentVersion = pluginConfig.getString(CONFIG_VERSION_PATH);
        if (currentVersion == null) {
            setConfigVersion(TECHNICAL_DEFAULT_VERSION);
            return true;
        }
        if (currentVersion.isEmpty()) {
            if (patchableVersions.isEmpty()) {
                setConfigVersion(TECHNICAL_DEFAULT_VERSION);
            } else {
                setConfigVersion(patchableVersions.lastEntry().getKey().getVersion());
            }
            return true;
        }
        return false;
    }

    private void setConfigVersion(final String technicalDefaultVersion) {
        pluginConfig.set(CONFIG_VERSION_PATH, technicalDefaultVersion);
        pluginConfig.setInlineComments(CONFIG_VERSION_PATH, List.of(VERSION_CONFIG_COMMENT));
    }

    @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
    private void buildVersionIndex(final ConfigurationSection section, final String previousKeys) throws InvalidConfigurationException {
        for (final String key : section.getKeys(false)) {
            final String currentKey = previousKeys.isEmpty() ? key : previousKeys + "." + key;

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
            patchableVersions.put(discoveredVersion, currentKey);
        }
    }

    /**
     * Patches the given config with the given patch file.
     *
     * @return whether the patch could be applied successfully
     */
    public boolean patch() {
        boolean noErrors = true;
        for (final Map.Entry<Version, String> versionData : patchableVersions.entrySet()) {
            final Version version = versionData.getKey();
            if (!comparator.isOtherNewerThanCurrent(configVersion, version)) {
                continue;
            }
            log.info("Applying patches to update to '" + version.getVersion() + "'...");
            final String patchDataPath = versionData.getValue();
            setConfigVersion(getNewVersion(patchDataPath));
            if (!applyPatch(patchDataPath)) {
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
        final List<Map<?, ?>> patchData = patchConfig.getMapList(patchDataPath);

        boolean noErrors = true;
        for (final Map<?, ?> transformationData : patchData) {
            final Map<String, String> typeSafeTransformationData = new HashMap<>();
            transformationData.forEach((key, value) -> typeSafeTransformationData.put(String.valueOf(key), String.valueOf(value)));
            final String raw = typeSafeTransformationData.get("type");
            if (raw == null) {
                log.warn("Missing transformation type for patcher '" + patchDataPath + "'!");
                continue;
            }
            final String transformationType = raw.toUpperCase(Locale.ROOT);
            try {
                applyTransformation(typeSafeTransformationData, transformationType);
            } catch (final PatchException e) {
                noErrors = false;
                log.info("Applying patch of type '" + transformationType + "'...");
                log.warn("There has been an issue while applying the patches for '" + patchDataPath + "': " + e.getMessage());
            }
        }
        return noErrors;
    }

    private void applyTransformation(final Map<String, String> transformationData, final String transformationType) throws PatchException {
        if (!transformers.containsKey(transformationType)) {
            throw new PatchException("Unknown transformation type '" + transformationType + "' used!");
        }
        transformers.get(transformationType).transform(transformationData, pluginConfig);
    }

    /**
     * Registers a new {@link PatchTransformer} that can be applied by the patcher.
     *
     * @param typeName    the name of the transformation type
     * @param transformer the transformer
     */
    public void registerTransformer(final String typeName, final PatchTransformer transformer) {
        transformers.put(typeName, transformer);
    }
}
