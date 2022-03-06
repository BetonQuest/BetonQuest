package org.betonquest.betonquest.api.config;

import org.betonquest.betonquest.modules.config.ConfigAccessorImpl;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * This interface defines methods to load, get, save and delete a config file or a resource from a plugin jar.
 */
public interface ConfigAccessor {

    /**
     * Loads a configurationFile.
     *
     * @param configurationFile the {@link File} that is represented by this {@link ConfigAccessorImpl}
     * @throws InvalidConfigurationException thrown if the configurationFile could not be loaded
     * @throws FileNotFoundException         thrown if the {@code configurationFile} could not be found
     */
    static ConfigAccessor create(final File configurationFile) throws InvalidConfigurationException, FileNotFoundException {
        return create(configurationFile, null, null);
    }

    /**
     * Tries to load a resourceFile.
     *
     * @param plugin       the plugin which is the source of the resource file
     * @param resourceFile the resource file to load from the plugin
     * @throws InvalidConfigurationException thrown if the resourceFile could not be loaded
     * @throws FileNotFoundException         thrown if the {@code resourceFile} could not be found
     */
    static ConfigAccessor create(final Plugin plugin, final String resourceFile) throws InvalidConfigurationException, FileNotFoundException {
        return create(null, plugin, resourceFile);
    }

    /**
     * Tries to load and save a configurationFile.
     * If the configurationFile does not exist, a fallback resourceFile will be loaded
     * and then saved as the configurationFile if given.
     *
     * @param configurationFile the {@link File} that is represented by this {@link ConfigAccessorImpl}
     * @param plugin            the plugin which is the source of the resource file
     * @param resourceFile      the resource file to load from the plugin
     * @throws InvalidConfigurationException thrown if the configurationFile or the resourceFile could not be loaded,
     *                                       or the resourceFile could not be saved to the configurationFile
     * @throws FileNotFoundException         thrown if the {@code configurationFile} or the {@code resourceFile}
     *                                       could not be found
     */
    static ConfigAccessor create(final File configurationFile, final Plugin plugin, final String resourceFile) throws InvalidConfigurationException, FileNotFoundException {
        return new ConfigAccessorImpl(configurationFile, plugin, resourceFile);
    }

    /**
     * Gets the {@link YamlConfiguration} that was loaded by this {@link ConfigAccessor}.
     *
     * @return the configuration.
     */
    YamlConfiguration getConfig();

    /**
     * Saves the file that is represented by this {@link ConfigAccessor}.
     * This method does nothing if no configurationFile was provided in the constructor.
     *
     * @return Only returns true if the file was saved.
     * @throws IOException thrown if the file could not be saved.
     */
    boolean save() throws IOException;

    /**
     * Delete the file that is represented by this {@link ConfigAccessor}.
     * This method does nothing if no configurationFile was provided in the constructor.
     *
     * @return Only returns true if the file was deleted and existed before.
     * @throws IOException thrown if the file could not be deleted.
     */
    boolean delete() throws IOException;

    /**
     * Reloads from the file that is represented by this {@link ConfigAccessor}.
     * This method does nothing if no configurationFile was provided in the constructor.
     *
     * @return Only returns true if the file was successfully reloaded.
     * @throws IOException thrown if the file could not be reloaded.
     */
    boolean reload() throws IOException;

    /**
     * Gets the {@link File}, that is represented by this {@link ConfigAccessor}.
     * Returns null, if the file is a resource from a plugin that is not saved as a file in the file system.
     *
     * @return the {@link File} if it exists
     */
    File getConfigurationFile();
}
