package org.betonquest.betonquest.modules.config;

import lombok.CustomLog;
import org.betonquest.betonquest.modules.config.patchTransformers.KeyRenameTransformation;
import org.betonquest.betonquest.modules.config.patchTransformers.ListEntryAddTransformation;
import org.betonquest.betonquest.modules.config.patchTransformers.ListEntryRemoveTransformation;
import org.betonquest.betonquest.modules.config.patchTransformers.ListEntryRenameTransformation;
import org.betonquest.betonquest.modules.config.patchTransformers.PatchTransformation;
import org.betonquest.betonquest.modules.config.patchTransformers.RemoveTransformation;
import org.betonquest.betonquest.modules.config.patchTransformers.SetTransformation;
import org.betonquest.betonquest.modules.config.patchTransformers.ValueRenameTransformation;
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

    public static final Pattern VERSION_PATTERN = Pattern.compile("(\\d*\\.\\d*\\.\\d*)\\.(\\d*)");
    private final ConfigurationSection pluginConfig;
    private final ConfigurationSection patch;

    private final Map<Version, String> versionIndex = new TreeMap(new VersionComparator(UpdateStrategy.MAJOR, "CONFIG-"));
    private final VersionComparator comparator = new VersionComparator(UpdateStrategy.MAJOR, "CONFIG-");
    private final Version configVersion;

    private final Map<String, PatchTransformation> transformers = new HashMap<>();

    public Patcher(final ConfigurationSection config, final ConfigurationSection patch) {
        this.pluginConfig = config;
        this.patch = patch;
        this.configVersion = new Version(config.getString("configVersion"));
        try {
            buildVersionIndex(this.patch, "");
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
                final Matcher m = VERSION_PATTERN.matcher(currentKey);
                if (m.matches()) {
                    final String result = m.group(1) + "-CONFIG-" + m.group(2);
                    final Version discoveredVersion = new Version(result);
                    if (comparator.isOtherNewerThanCurrent(configVersion, discoveredVersion)) {
                        versionIndex.put(discoveredVersion, currentKey);
                    }
                }
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
            try {
                applyPatch(key);
                pluginConfig.set("configVersion", getNewVersion(key));
            } catch (final Exception placeholder) {
                LOG.error("Failed patching the config file. Your BetonQuest config may now be in an invalid state!");
                return false;
            }
        }
        return true;
    }

    private String getNewVersion(final String key) {
        final int lastPoint = key.lastIndexOf('.');
        final String first = key.substring(0, lastPoint);
        final String second = key.substring(lastPoint + 1);
        return first + "-CONFIG-" + second;
    }

    private boolean applyPatch(final String patchDataPath) {
        final var patchData = patch.getMapList(patchDataPath);
        patchData.forEach(transformationData -> {
            final Map<String, String> typeSafeTransformationData = new HashMap<>();
            transformationData.forEach((key, value) -> {
                typeSafeTransformationData.put(String.valueOf(key), String.valueOf(value));
            });

            final String transformationType = transformationData.get("type").toString().toUpperCase(Locale.ROOT);
            applyTransformation(typeSafeTransformationData, transformationType);
        });
        return true;
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
