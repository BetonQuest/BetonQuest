package org.betonquest.betonquest.api.config;

import org.betonquest.betonquest.config.ConfigAccessorImpl;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

/**
 * This interface defines the methods to load get save and delete a config file or a resource from a plugin jar.
 */
public interface ConfigAccessor {

    /**
     * This tries to load a configurationFile.
     *
     * @param configurationFile the {@link File} that is represented by this {@link ConfigAccessorImpl}
     * @throws InvalidConfigurationException Is thrown if the configurationFile could not be loaded
     */
    static ConfigAccessor create(final File configurationFile) throws InvalidConfigurationException {
        return create(configurationFile, null, null);
    }

    /**
     * This tries to load a resourceFile.
     *
     * @param plugin       the plugin where the resource file comes from
     * @param resourceFile the resource file to load from the plugin
     * @throws InvalidConfigurationException Is thrown if the resourceFile could not be loaded
     */
    static ConfigAccessor create(final Plugin plugin, final String resourceFile) throws InvalidConfigurationException {
        return create(null, plugin, resourceFile);
    }

    /**
     * This tries to load a configurationFile or resourceFile and saves it.
     * If the configurationFile does not exist the resourceFile will be loaded
     * and then saved to the configurationFile if given.
     *
     * @param configurationFile the {@link File} that is represented by this {@link ConfigAccessorImpl}
     * @param plugin            the plugin where the resource file comes from
     * @param resourceFile      the resource file to load from the plugin
     * @throws InvalidConfigurationException Is thrown if the configurationFile or the resourceFile could not be loaded,
     *                                       or the resourceFile could not be saved to the configurationFile
     */
    static ConfigAccessor create(final File configurationFile, final Plugin plugin, final String resourceFile) throws InvalidConfigurationException {
        return new ConfigAccessorImpl(configurationFile, plugin, resourceFile);
    }

    /**
     * Get the {@link YamlConfiguration} that was loaded by this {@link ConfigAccessor}.
     *
     * @return the configuration.
     */
    YamlConfiguration getConfig();

    /**
     * Saves the file that is represented by this {@link ConfigAccessor}.
     * If no configurationFile was provided in the constructor, this method does nothing.
     *
     * @return Only returns true, if the file was saved.
     * @throws IOException Is thrown if the file could not be saved.
     */
    boolean save() throws IOException;

    /**
     * Delete the file that is represented by this {@link ConfigAccessor}.
     * If no configurationFile was provided in the constructor, this method does nothing.
     *
     * @return Only returns true, if the file was deleted and did exist before.
     */
    boolean delete();
}
