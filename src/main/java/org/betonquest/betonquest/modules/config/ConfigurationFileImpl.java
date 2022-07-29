package org.betonquest.betonquest.modules.config;

import lombok.CustomLog;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.bukkit.config.custom.ConfigurationSectionDecorator;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.config.ConfigurationFile;
import org.betonquest.betonquest.utils.Utils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Facade for easy loading and saving of configs.
 */
@CustomLog(topic = "ConfigurationFile")
public final class ConfigurationFileImpl extends ConfigurationSectionDecorator implements ConfigurationFile {

    /**
     * Holds the config file.
     */
    private final ConfigAccessor accessor;

    /**
     * Creates a new {@link ConfigurationFileImpl}.
     * Patches the configuration file held by the {@code accessor} with the patch file from the {@code patchAccessor}.
     * <br>
     * See {@link ConfigurationFile#create} for more information.
     *
     * @param accessor      a {@link ConfigAccessor} that holds the config file
     * @param patchAccessor a {@link ConfigAccessor} that holds the patch file
     * @throws InvalidConfigurationException if patch modifications couldn't be saved
     */
    private ConfigurationFileImpl(final ConfigAccessor accessor, final ConfigAccessor patchAccessor) throws InvalidConfigurationException {
        super(accessor.getConfig());
        this.accessor = accessor;
        if (patchAccessor != null && patchConfig(patchAccessor.getConfig())) {
            try {
                accessor.save();
            } catch (final IOException e) {
                throw new InvalidConfigurationException("The configuration file was patched but could not be saved! Reason: " + e.getMessage(), e);
            }
        }
    }

    /**
     * @see ConfigurationFile#create(File, Plugin, String)
     */
    public static ConfigurationFile create(final File configurationFile, final Plugin plugin, final String resourceFile) throws InvalidConfigurationException, FileNotFoundException {
        if (configurationFile == null || plugin == null || resourceFile == null) {
            throw new IllegalArgumentException("The configurationFile, plugin and resourceFile must be defined but were null.");
        }

        final ConfigAccessor accessor = ConfigAccessor.create(configurationFile, plugin, resourceFile);
        final ConfigAccessor resourceAccessor = ConfigAccessor.create(plugin, resourceFile);
        accessor.getConfig().setDefaults(resourceAccessor.getConfig());
        accessor.getConfig().options().copyDefaults(true);
        try {
            accessor.save();
        } catch (final IOException e) {
            throw new InvalidConfigurationException("Default values were applied to the config but could not be saved! Reason: " + e.getMessage(), e);
        }
        final ConfigAccessor patchAccessor = createPatchAccessor(plugin, resourceFile);
        return new ConfigurationFileImpl(accessor, patchAccessor);
    }

    private static ConfigAccessor createPatchAccessor(final Plugin plugin, final String resourceFile) throws InvalidConfigurationException {
        int index = resourceFile.lastIndexOf('.');
        final int separatorIndex = resourceFile.lastIndexOf(File.pathSeparator);
        if (index < separatorIndex) {
            index = -1;
        }
        if (index == -1) {
            index = resourceFile.length();
        }
        final String resourceFilePatch = resourceFile.substring(0, index) + ".patch" + resourceFile.substring(index);
        try {
            return ConfigAccessor.create(plugin, resourceFilePatch);
        } catch (final FileNotFoundException e) {
            LOG.debug(e.getMessage(), e);
        }
        return null;
    }

    /**
     * Patches the config with the given patch config.
     *
     * @param patchAccessorConfig the config that contains patches
     * @return if the file was modified
     */
    @SuppressWarnings("PMD.UnusedFormalParameter")
    private boolean patchConfig(final ConfigurationSection patchAccessorConfig) {
        final Patcher patcher = new Patcher(accessor.getConfig(), patchAccessorConfig);

        if (!patcher.hasUpdate()) {
            LOG.debug("No patch found.");
            return false;
        } else {
            final String configName = accessor.getConfigurationFile().getName();
            final String currentVersion = accessor.getConfig().getString("configVersion", "2.0.0-CONFIG-0");
            LOG.info("Patch for configuration '%s' with current version '%s' found.".formatted(configName, currentVersion));
            LOG.info("Backing up current config...");

            final File dataFolder = BetonQuest.getInstance().getDataFolder();
            Utils.backup(dataFolder, accessor.getConfig(), false);

            final boolean flawless = patcher.patch();
            if (flawless) {
                LOG.info("Patching complete!");
            } else {
                LOG.warn("The patching progress did not go flawlessly. However, this does not mean your configs " +
                        "are now corrupted. Please check the errors above to see what the patcher did. " +
                        "You might want to adjust your config manually depending on that information.");
            }
            return true;
        }
    }

    @Override
    public boolean save() throws IOException {
        return accessor.save();
    }

    @Override
    public boolean delete() throws IOException {
        return accessor.delete();
    }

    @Override
    public boolean reload() throws IOException {
        if (accessor.reload()) {
            original = accessor.getConfig();
            return true;
        }
        return false;
    }
}
