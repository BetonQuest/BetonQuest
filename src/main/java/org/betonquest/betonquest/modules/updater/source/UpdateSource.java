package org.betonquest.betonquest.modules.updater.source;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * This is an interface that provides basic methods to read update information from a given {@link URL}.
 */
@SuppressWarnings("PMD.AbstractClassWithoutAbstractMethod")
public abstract class UpdateSource {

    /**
     * The response code 403 as a constant.
     */
    public static final int RESPONSE_CODE_403 = 403;

    /**
     * Default constructor
     */
    public UpdateSource() {
        // Empty
    }

    /**
     * This method can read a String from a given {@link URL}.
     *
     * @param url that should be read as {@link String}
     * @return the {@link String} represented by the given {@link URL}
     * @throws IOException is thrown if any problem occurred while reading the String from the {@link URL}
     */
    public final String readStringFromURL(final URL url) throws IOException {
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
     * By default, it will ignore all response codes.
     *
     * @param responseCode the response code that occurred
     * @throws IOException This can be thrown, if the response code prevents a successful download.
     */
    @SuppressWarnings("PMD.EmptyMethodInAbstractClassShouldBeAbstract")
    protected void handleResponseCode(final int responseCode) throws IOException {
        // default behaviour is to ignore the response code
    }
}
