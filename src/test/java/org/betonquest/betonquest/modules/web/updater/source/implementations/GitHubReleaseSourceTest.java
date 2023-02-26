package org.betonquest.betonquest.modules.web.updater.source.implementations;

import org.betonquest.betonquest.modules.versioning.Version;
import org.betonquest.betonquest.modules.web.updater.source.UpdateSource;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * This class tests the {@link GitHubReleaseSource}.
 */
class GitHubReleaseSourceTest {

    @SuppressWarnings({"PMD.JUnitTestContainsTooManyAsserts"})
    @Test
    void testGitHubReleaseSource() throws IOException {
        final Path filePath = Path.of("src/test/resources/modules/web/updater/github.json");
        final String apiUrl = "https://github.com/BetonQuest/BetonQuest";
        final String apiUrlReleases = "https://github.com/BetonQuest/BetonQuest" + GitHubReleaseSource.RELEASES_URL;

        final GitHubReleaseSource releaseSource = spy(new GitHubReleaseSource(apiUrl));
        doReturn(Files.readString(filePath)).when(releaseSource).readStringFromURL(new URL(apiUrlReleases));
        final Map<Version, String> versions = releaseSource.getReleaseVersions();

        assertEquals(2, versions.size(), "Expected two versions from getReleaseVersions");
        final String url1 = versions.get(new Version("1.12.0"));
        assertEquals(apiUrlReleases + "/download/v1.12.0/BetonQuest.jar", url1, "The download URL is not correct");
        final String url2 = versions.get(new Version("1.12.1"));
        assertEquals(apiUrlReleases + "/download/v1.12.1/BetonQuest.jar", url2, "The download URL is not correct");
    }

    @Test
    void testReadStringFromURLThrowsException() {
        final String apiUrl = "https://github.com/BetonQuest/BetonQuest/releases/download";
        final GitHubReleaseSource handler = new GitHubReleaseSource(apiUrl);

        final IOException exception = assertThrowsExactly(IOException.class, () -> handler.handleResponseCode(UpdateSource.RESPONSE_CODE_403), "Expected IOException");
        assertEquals("It looks like too many requests were made to the update server, please wait until you have been unblocked.", exception.getMessage(), "Exception messages are not equal");

    }
}
