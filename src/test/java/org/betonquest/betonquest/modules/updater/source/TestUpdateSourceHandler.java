package org.betonquest.betonquest.modules.updater.source;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TestUpdateSourceHandler {
    @Test
    void testReadStringFromURL() throws IOException {
        final File fileUrl = new File("src/test/resources/modules/updater/latest.json");

        final UpdateSourceHandler handler = mock(UpdateSourceHandler.class);
        when(handler.readStringFromURL(any())).thenCallRealMethod();
        final URL url = mock(URL.class);
        final HttpURLConnection httpURLConnection = mock(HttpURLConnection.class);
        when(httpURLConnection.getInputStream()).thenReturn(new FileInputStream(fileUrl));
        when(url.openConnection()).thenReturn(httpURLConnection);

        final String response = handler.readStringFromURL(url);
        final String version = """
                {
                  1.12.0: "410",
                  1.12.1: "3"
                }
                """;
        assertEquals(version, response);
    }
}
