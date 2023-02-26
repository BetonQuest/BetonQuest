package org.betonquest.betonquest.modules.web.updater.source.implementations;

import org.betonquest.betonquest.modules.versioning.Version;
import org.betonquest.betonquest.modules.web.ContentSource;
import org.betonquest.betonquest.modules.web.WebContentSource;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

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

        final ContentSource contentSource = url -> {
            if (url.toString().equals(apiUrlReleases)) {
                return Files.readString(filePath);
            }
            throw new IOException("Unexpected URL: " + url);
        };
        final GitHubReleaseSource releaseSource = new GitHubReleaseSource(apiUrl, contentSource);
        final Map<Version, String> versions = releaseSource.getReleaseVersions();

        assertEquals(2, versions.size(), "Expected two versions from getReleaseVersions");
        final String url1 = versions.get(new Version("1.12.0"));
        assertEquals(apiUrlReleases + "/download/v1.12.0/BetonQuest.jar", url1, "The download URL is not correct");
        final String url2 = versions.get(new Version("1.12.1"));
        assertEquals(apiUrlReleases + "/download/v1.12.1/BetonQuest.jar", url2, "The download URL is not correct");
    }

    @Test
    void testReadStringFromURLThrowsException() {
        final WebContentSource.HTTPCodeHandler handler = GitHubReleaseSource.HTTP_CODE_HANDLER;

        final IOException exception = assertThrowsExactly(IOException.class, () -> handler.handle(GitHubReleaseSource.RESPONSE_CODE_403), "Expected IOException");
        assertEquals("It looks like too many requests were made to the update server, please wait until you have been unblocked.", exception.getMessage(), "Exception messages are not equal");

    }
}
