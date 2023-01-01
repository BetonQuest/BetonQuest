package org.betonquest.betonquest.modules.updater.source.implementations;

import org.betonquest.betonquest.modules.versioning.Version;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * This class tests the {@link NexusReleaseAndDevelopmentSource}.
 */
class NexusReleaseAndDevelopmentSourceTest {

    @SuppressWarnings({"PMD.JUnitTestContainsTooManyAsserts"})
    @Test
    void testNexusReleaseSource() throws IOException {
        final Path filePathShadedPage1 = Path.of("src/test/resources/modules/updater/nexusShadedPage1.json");
        final Path filePathShadedPage2 = Path.of("src/test/resources/modules/updater/nexusShadedPage2.json");
        final String apiUrl = "https://betonquest.org/nexus";
        final String apiUrlShadedPage1 = apiUrl + NexusReleaseAndDevelopmentSource.SERVICE_REST_V_1 + NexusReleaseAndDevelopmentSource.SEARCH_SHADED;
        final String apiUrlShadedPage2 = apiUrl + NexusReleaseAndDevelopmentSource.SERVICE_REST_V_1 + NexusReleaseAndDevelopmentSource.SEARCH_SHADED + NexusReleaseAndDevelopmentSource.CONTINUATION_TOKEN + "2";

        final NexusReleaseAndDevelopmentSource releaseSource = spy(new NexusReleaseAndDevelopmentSource(apiUrl));
        doReturn(Files.readString(filePathShadedPage1)).when(releaseSource).readStringFromURL(new URL(apiUrlShadedPage1));
        doReturn(Files.readString(filePathShadedPage2)).when(releaseSource).readStringFromURL(new URL(apiUrlShadedPage2));
        final Map<Version, String> versions = releaseSource.getReleaseVersions();

        assertEquals(2, versions.size(), "Expected two versions from getReleaseVersions");
        final String url1 = versions.get(new Version("1.12.4"));
        assertEquals(apiUrl + "/repository/betonquest/org/betonquest/betonquest/1.12.4/betonquest-1.12.4-shaded.jar", url1,
                "The download URL is not correct");
        final String url2 = versions.get(new Version("1.12.5"));
        assertEquals(apiUrl + "/repository/betonquest/org/betonquest/betonquest/1.12.5/betonquest-1.12.5-shaded.jar", url2,
                "The download URL is not correct");
    }

    @SuppressWarnings({"PMD.JUnitTestContainsTooManyAsserts"})
    @Test
    void testNexusDevelopementSource() throws IOException {
        final Path filePathPomPage1 = Path.of("src/test/resources/modules/updater/nexusPomPage1.json");
        final Path filePathPomPage2 = Path.of("src/test/resources/modules/updater/nexusPomPage2.json");
        final Path filePathPom1 = Path.of("src/test/resources/modules/updater/nexusPom1.xml");
        final Path filePathPom2 = Path.of("src/test/resources/modules/updater/nexusPom2.xml");
        final String apiUrl = "https://betonquest.org/nexus";
        final String apiUrlPomPage1 = apiUrl + NexusReleaseAndDevelopmentSource.SERVICE_REST_V_1 + NexusReleaseAndDevelopmentSource.SEARCH_POM;
        final String apiUrlPomPage2 = apiUrl + NexusReleaseAndDevelopmentSource.SERVICE_REST_V_1 + NexusReleaseAndDevelopmentSource.SEARCH_POM + NexusReleaseAndDevelopmentSource.CONTINUATION_TOKEN + "2";
        final String apiUrlPom1 = apiUrl + "/repository/betonquest/org/betonquest/betonquest/2.0.0-SNAPSHOT/betonquest-2.0.0-20221230.085132-398.pom";
        final String apiUrlPom2 = apiUrl + "/repository/betonquest/org/betonquest/betonquest/1.12.10-SNAPSHOT/betonquest-1.12.10-20221230.085132-398.pom";

        final NexusReleaseAndDevelopmentSource releaseSource = spy(new NexusReleaseAndDevelopmentSource(apiUrl));
        doReturn(Files.readString(filePathPomPage1)).when(releaseSource).readStringFromURL(new URL(apiUrlPomPage1));
        doReturn(Files.readString(filePathPomPage2)).when(releaseSource).readStringFromURL(new URL(apiUrlPomPage2));
        doReturn(Files.readString(filePathPom1)).when(releaseSource).readStringFromURL(new URL(apiUrlPom1));
        doReturn(Files.readString(filePathPom2)).when(releaseSource).readStringFromURL(new URL(apiUrlPom2));
        final Map<Version, String> versions = releaseSource.getDevelopmentVersions();

        assertEquals(2, versions.size(), "Expected two versions from getReleaseVersions");
        final String url1 = versions.get(new Version("2.0.0-DEV-495"));
        assertEquals(apiUrlPom1.replace(".pom", "-shaded.jar"), url1, "The download URL is not correct");
        final String url2 = versions.get(new Version("1.12.10-DEV-495"));
        assertEquals(apiUrlPom2.replace(".pom", "-shaded.jar"), url2, "The download URL is not correct");
    }
}
