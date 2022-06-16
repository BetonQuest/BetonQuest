package org.betonquest.betonquest.modules.config;

import java.io.IOException;

/**
 * This interface describes something that can update a value of the configuration.
 *
 * @param <T> the type of the configuration value
 */
public interface ConfigUpdater<T> {

    /**
     * Update the configuration value.
     *
     * @param value the new value
     * @throws IOException when persisting the changed value fails
     */
    void update(T value) throws IOException;
}
