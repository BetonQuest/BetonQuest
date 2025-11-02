package org.betonquest.betonquest.web;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * This class implements the {@link ContentSource} interface and provides a default implementation for web content.
 */
public final class WebContentSource implements ContentSource {

    /**
     * The {@link HTTPCodeHandler} to use.
     */
    private final HTTPCodeHandler handler;

    /**
     * Constructs a new {@link WebContentSource}, ignoring all http response codes.
     */
    public WebContentSource() {
        this(code -> {
        });
    }

    /**
     * Creates a new {@link WebContentSource} with the given {@link HTTPCodeHandler}.
     *
     * @param handler handle http response codes
     */
    public WebContentSource(final HTTPCodeHandler handler) {
        this.handler = handler;
    }

    /**
     * This method can read a String from a given {@link URL}.
     *
     * @param url that should be read as {@link String}
     * @return the {@link String} represented by the given {@link URL}
     * @throws IOException is thrown if any problem occurred while reading the String from the {@link URL}
     */
    @Override
    public String get(final URL url) throws IOException {
        final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.connect();
        try {
            handler.handle(connection.getResponseCode());
            try (InputStream inputStream = connection.getInputStream()) {
                return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            }
        } finally {
            connection.disconnect();
        }
    }

    /**
     * This interface can be used to handle HTTP response codes.
     */
    @FunctionalInterface
    public interface HTTPCodeHandler {

        /**
         * Handle response codes of all incoming http requests.
         *
         * @param responseCode the response code that occurred
         * @throws IOException This can be thrown, if the response code prevents a successful download.
         */
        void handle(int responseCode) throws IOException;
    }
}
