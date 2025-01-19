package org.betonquest.betonquest.web;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * This class tests the {@link WebContentSource}.
 */
@SuppressWarnings("Convert2Lambda")
class WebContentSourceTest {

    /**
     * Response code 403.
     */
    private static final int RESPONSE_CODE_403 = 403;

    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    @Test
    void testGet() throws IOException {
        final Path filePath = Path.of("src/test/resources/modules/web/updater/latest.json");

        final WebContentSource.HTTPCodeHandler handler = spy(new WebContentSource.HTTPCodeHandler() {
            @Override
            public void handle(final int code) {
                // Empty
            }
        });
        final WebContentSource contentSource = new WebContentSource(handler);
        final URL url = mock(URL.class);
        final HttpURLConnection httpURLConnection = mock(HttpURLConnection.class);
        when(httpURLConnection.getInputStream()).thenReturn(Files.newInputStream(filePath));
        when(url.openConnection()).thenReturn(httpURLConnection);
        when(httpURLConnection.getResponseCode()).thenReturn(RESPONSE_CODE_403);

        final String response = contentSource.get(url);
        final String version = """
                {
                  1.12.0: "410",
                  1.12.1: "3"
                }
                """;
        assertEquals(version, getFormattedMessage(response), "Content from readStringFromURL does not match expectation");

        verify(httpURLConnection, times(1)).connect();
        verify(httpURLConnection, times(1)).disconnect();
        verify(handler, times(1)).handle(RESPONSE_CODE_403);
    }

    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    @Test
    void testGetThrowsException() throws IOException {
        final IOException ioException = new IOException("Test IOException");

        final WebContentSource.HTTPCodeHandler handler = spy(new WebContentSource.HTTPCodeHandler() {
            @Override
            public void handle(final int code) throws IOException {
                if (code == RESPONSE_CODE_403) {
                    throw ioException;
                }
            }
        });
        final WebContentSource contentSource = new WebContentSource(handler);
        final URL url = mock(URL.class);
        final HttpURLConnection httpURLConnection = mock(HttpURLConnection.class);
        when(url.openConnection()).thenReturn(httpURLConnection);
        when(httpURLConnection.getResponseCode()).thenReturn(RESPONSE_CODE_403);

        final IOException exception = assertThrowsExactly(IOException.class, () -> contentSource.get(url), "Expected IOException");
        assertEquals(ioException.getMessage(), exception.getMessage(), "Exception messages are not equal");

        verify(httpURLConnection, times(1)).connect();
        verify(httpURLConnection, times(1)).disconnect();
        verify(handler, times(1)).handle(RESPONSE_CODE_403);
    }

    private String getFormattedMessage(final String message) {
        return message.replace("\r\n", "\n").replace("\r", "\n");
    }
}
