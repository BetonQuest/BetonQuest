package org.betonquest.betonquest.modules.config;

import lombok.CustomLog;
import org.betonquest.betonquest.api.bukkit.config.custom.ConfigurationSectionDecorator;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.config.ConfigurationFile;
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

    @SuppressWarnings("PMD.UnusedFormalParameter")
    private boolean patchConfig(final ConfigurationSection patchAccessorConfig) {
        return false;
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
