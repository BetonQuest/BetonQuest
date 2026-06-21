package org.betonquest.betonquest.api.config;

import java.io.File;
import java.io.IOException;

/**
 * Represents a configuration file that can be accessed, saved, deleted and reloaded.
 *
 * @since 3.0.0
 */
public interface FileConfigAccessor extends ConfigAccessor {

    /**
     * Saves the file that is represented by this {@link FileConfigAccessor}.
     * This method does nothing if no configurationFile was provided in the constructor.
     *
     * @return Only returns true if the file was saved.
     * @throws IOException if the file could not be saved.
     * @since 3.0.0
     */
    boolean save() throws IOException;

    /**
     * Delete the file that is represented by this {@link FileConfigAccessor}.
     * This method does nothing if no configurationFile was provided in the constructor.
     *
     * @return Only returns true if the file was deleted and existed before.
     * @throws IOException if the file could not be deleted.
     * @since 3.0.0
     */
    boolean delete() throws IOException;

    /**
     * Reloads from the file that is represented by this {@link FileConfigAccessor}.
     * This method does nothing if no configurationFile was provided in the constructor.
     *
     * @throws IOException           if an exception was thrown during the reload.
     * @throws IllegalStateException if there is no configuration file to be reloaded.
     * @since 3.0.0
     */
    void reload() throws IOException;

    /**
     * Gets the {@link File}, that is represented by this {@link FileConfigAccessor}.
     *
     * @return the {@link File}
     * @since 3.0.0
     */
    File getConfigurationFile();
}
