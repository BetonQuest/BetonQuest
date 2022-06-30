package org.betonquest.betonquest.modules.updater.source.implementations;

import org.betonquest.betonquest.modules.updater.source.UpdateSource;
import org.betonquest.betonquest.modules.versioning.Version;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * This class test the {@link GitHubReleaseSource}.
 */
class GitHubReleaseSourceTest {

    @SuppressWarnings({"PMD.JUnitTestContainsTooManyAsserts"})
    @Test
    void testGitHubReleaseSource() throws IOException {
        final Path filePath = Path.of("src/test/resources/modules/updater/github.json");
        final String apiUrl = "https://github.com/BetonQuest/BetonQuest/releases/download";

        final GitHubReleaseSource releaseSource = spy(new GitHubReleaseSource(apiUrl));
        doReturn(Files.readString(filePath)).when(releaseSource).readStringFromURL(new URL(apiUrl));
        final Map<Version, String> versions = releaseSource.getReleaseVersions();

        assertEquals(2, versions.size(), "Expected two versions from getReleaseVersions");
        final String url1 = versions.get(new Version("1.12.0"));
        assertEquals(apiUrl + "/v1.12.0/BetonQuest.jar", url1, "The download URL is not correct");
        final String url2 = versions.get(new Version("1.12.1"));
        assertEquals(apiUrl + "/v1.12.1/BetonQuest.jar", url2, "The download URL is not correct");
    }

    @Test
    void testReadStringFromURLThrowsException() throws IOException {
        final String apiUrl = "https://github.com/BetonQuest/BetonQuest/releases/download";
        final IOException ioException = new IOException("Test IOException");

        final GitHubReleaseSource handler = spy(new GitHubReleaseSource(apiUrl));
        final URL url = mock(URL.class);
        final HttpURLConnection httpURLConnection = mock(HttpURLConnection.class);
        when(url.openConnection()).thenReturn(httpURLConnection);
        when(httpURLConnection.getResponseCode()).thenReturn(UpdateSource.RESPONSE_CODE_403);

        assertThrowsExactly(IOException.class, () -> handler.readStringFromURL(url), ioException.getMessage());

        verify(httpURLConnection, times(1)).connect();
        verify(httpURLConnection, times(1)).disconnect();
        verify(handler, times(1)).handleResponseCode(UpdateSource.RESPONSE_CODE_403);
    }
}
