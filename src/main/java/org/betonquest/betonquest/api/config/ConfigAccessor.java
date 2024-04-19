package org.betonquest.betonquest.api.config;

import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.UnknownNullability;

import java.io.File;
import java.io.IOException;

/**
 * This interface defines methods to load, get, save and delete a config file or a resource from a plugin jar.
 */
public interface ConfigAccessor {

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
    @UnknownNullability
    File getConfigurationFile();
}
