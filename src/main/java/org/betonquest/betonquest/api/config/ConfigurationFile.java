package org.betonquest.betonquest.api.config;

import org.bukkit.configuration.ConfigurationSection;

import java.io.IOException;

/**
 * Facade to make configuration file handling easier.
 */
public interface ConfigurationFile extends ConfigurationSection {

    /**
     * Saves the file that is represented by this {@link ConfigAccessor}.
     * This method does nothing if no configurationFile was provided in the constructor.
     *
     * @return Only returns true if the file was saved.
     * @throws IOException if the file could not be saved.
     */
    boolean save() throws IOException;

    /**
     * Delete the file that is represented by this {@link ConfigAccessor}.
     * This method does nothing if no configurationFile was provided in the constructor.
     *
     * @return Only returns true if the file was deleted and existed before.
     * @throws IOException if the file could not be deleted.
     */
    boolean delete() throws IOException;

    /**
     * Reloads from the file that is represented by this {@link ConfigAccessor}.
     * This method does nothing if no configurationFile was provided in the constructor.
     *
     * @return Only returns true if the file was successfully reloaded.
     * @throws IOException if the file could not be reloaded.
     */
    boolean reload() throws IOException;
}
