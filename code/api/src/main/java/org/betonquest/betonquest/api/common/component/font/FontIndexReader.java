package org.betonquest.betonquest.api.common.component.font;

import java.io.IOException;
import java.io.InputStream;

/**
 * The FontIndexReader interface represents a function that reads a font index file to a {@link Font}.
 *
 * @since 3.0.0
 */
@FunctionalInterface
public interface FontIndexReader {

    /**
     * Reads an {@link InputStream} to a {@link Font}.
     * The provided {@link InputStream}'s data must conform to the readers expected format.
     * The {@link InputStream} must be closed by the caller.
     *
     * @param inputStream the input stream to read from
     * @return a valid {@link Font} instance representing the data provided in {@link InputStream}
     * @throws IOException if an error occurs while reading from the {@link InputStream}
     * @since 3.0.0
     */
    Font read(InputStream inputStream) throws IOException;
}
