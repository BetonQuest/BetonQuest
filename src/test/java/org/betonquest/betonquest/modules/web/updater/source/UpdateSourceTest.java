package org.betonquest.betonquest.modules.web.updater.source;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * This class tests the {@link UpdateSource}.
 */
class UpdateSourceTest {

    @Test
    void testReadStringFromURL() throws IOException {
        final Path filePath = Path.of("src/test/resources/modules/web/updater/latest.json");

        final UpdateSource handler = mock(UpdateSource.class);
        when(handler.readStringFromURL(any())).thenCallRealMethod();
        doCallRealMethod().when(handler).handleResponseCode(anyInt());
        final URL url = mock(URL.class);
        final HttpURLConnection httpURLConnection = mock(HttpURLConnection.class);
        when(httpURLConnection.getInputStream()).thenReturn(Files.newInputStream(filePath));
        when(url.openConnection()).thenReturn(httpURLConnection);
        when(httpURLConnection.getResponseCode()).thenReturn(UpdateSource.RESPONSE_CODE_403);

        final String response = handler.readStringFromURL(url);
        final String version = """
                {
                  1.12.0: "410",
                  1.12.1: "3"
                }
                """;
        assertEquals(version, getFormattedMessage(response), "Content from readStringFromURL does not match expectation");

        verify(httpURLConnection, times(1)).connect();
        verify(httpURLConnection, times(1)).disconnect();
        verify(handler, times(1)).handleResponseCode(UpdateSource.RESPONSE_CODE_403);
    }

    @Test
    void testReadStringFromURLThrowsException() throws IOException {
        final IOException ioException = new IOException("Test IOException");

        final UpdateSource handler = mock(UpdateSource.class);
        when(handler.readStringFromURL(any())).thenCallRealMethod();
        doThrow(ioException).when(handler).handleResponseCode(UpdateSource.RESPONSE_CODE_403);
        final URL url = mock(URL.class);
        final HttpURLConnection httpURLConnection = mock(HttpURLConnection.class);
        when(url.openConnection()).thenReturn(httpURLConnection);
        when(httpURLConnection.getResponseCode()).thenReturn(UpdateSource.RESPONSE_CODE_403);

        final IOException exception = assertThrowsExactly(IOException.class, () -> handler.readStringFromURL(url), "Expected IOException");
        assertEquals(ioException.getMessage(), exception.getMessage(), "Exception messages are not equal");

        verify(httpURLConnection, times(1)).connect();
        verify(httpURLConnection, times(1)).disconnect();
        verify(handler, times(1)).handleResponseCode(UpdateSource.RESPONSE_CODE_403);
    }

    private String getFormattedMessage(final String message) {
        return message.replace("\r\n", "\n").replace("\r", "\n");
    }
}
