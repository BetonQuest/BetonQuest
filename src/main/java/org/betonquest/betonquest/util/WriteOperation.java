package org.betonquest.betonquest.util;

import java.io.IOException;

/**
 * This interface describes something that can write a value but might fail with a {@link IOException}.
 *
 * @param <T> the type of the value
 */
public interface WriteOperation<T> {

    /**
     * Write the value.
     *
     * @param value the value
     * @throws IOException when writing the value fails
     */
    void write(T value) throws IOException;
}
