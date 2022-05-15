package org.betonquest.betonquest.modules.updater.source;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public interface UpdateSourceHandler {
    default String readStringFromURL(final String stringUrl) throws IOException {
        final URL url = new URL(stringUrl);
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

    default void handleResponseCode(final int responseCode) throws IOException {
    }

}
