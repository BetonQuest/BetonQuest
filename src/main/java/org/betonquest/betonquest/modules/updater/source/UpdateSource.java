package org.betonquest.betonquest.modules.updater.source;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * This is an interface that give basic methods to read information from a given {@link URL} to get update information.
 */
public interface UpdateSource {
    /**
     * The response code 403 as a constant
     */
    int RESPONSE_CODE_403 = 403;

    /**
     * This method can read s String from a given {@link URL}.
     *
     * @param url that should be read as {@link String}
     * @return the {@link String} represented by the given {@link URL}
     * @throws IOException is thrown if any problem occurred while reading the String from the {@link URL}
     */
    default String readStringFromURL(final URL url) throws IOException {
        final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.connect();
        try {
            handleResponseCode(connection.getResponseCode());
            try (InputStream inputStream = connection.getInputStream()) {
                return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            }
        } finally {
            connection.disconnect();
        }
    }

    /**
     * This method is called from the default implementation of the {@link UpdateSource#readStringFromURL(URL)} method.
     * In default, it will ignore oll response codes.
     *
     * @param responseCode the response code that occurred
     * @throws IOException This can be thrown, if the response code prevents a successful download.
     */
    default void handleResponseCode(final int responseCode) throws IOException {
        // Empty
    }

}
