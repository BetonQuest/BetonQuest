package org.betonquest.betonquest.modules.web.updater.source.implementations;

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
    /**
     * The path to the root page for a specific Nexus.
     */
    public static final String API_URL = "https://betonquest.org/nexus";

    private static NexusReleaseAndDevelopmentSource getMockedNexusSource() throws IOException {
        final Path filePathShadedPage1 = Path.of("src/test/resources/modules/web/updater/nexusShadedPage1.json");
        final Path filePathShadedPage2 = Path.of("src/test/resources/modules/web/updater/nexusShadedPage2.json");
        final String apiUrlShadedPage1 = API_URL + NexusReleaseAndDevelopmentSource.SERVICE_REST_V_1 + NexusReleaseAndDevelopmentSource.SEARCH_URL;
        final String apiUrlShadedPage2 = API_URL + NexusReleaseAndDevelopmentSource.SERVICE_REST_V_1 + NexusReleaseAndDevelopmentSource.SEARCH_URL + NexusReleaseAndDevelopmentSource.CONTINUATION_TOKEN + "2";

        final NexusReleaseAndDevelopmentSource source = spy(new NexusReleaseAndDevelopmentSource(API_URL));
        doReturn(Files.readString(filePathShadedPage1)).when(source).readStringFromURL(new URL(apiUrlShadedPage1));
        doReturn(Files.readString(filePathShadedPage2)).when(source).readStringFromURL(new URL(apiUrlShadedPage2));
        return source;
    }

    @SuppressWarnings({"PMD.JUnitTestContainsTooManyAsserts"})
    @Test
    void testNexusReleaseSource() throws IOException {
        final Map<Version, String> versions = getMockedNexusSource().getReleaseVersions();

        assertEquals(3, versions.size(), "Expected two versions from getReleaseVersions");
        final String url1 = versions.get(new Version("1.12.4"));
        assertEquals(API_URL + "/repository/betonquest/org/betonquest/betonquest/1.12.4/betonquest-1.12.4-shaded.jar", url1,
                "The download URL is not correct");
        final String url2 = versions.get(new Version("1.12.5"));
        assertEquals(API_URL + "/repository/betonquest/org/betonquest/betonquest/1.12.5/betonquest-1.12.5-shaded.jar", url2,
                "The download URL is not correct");
        final String url3 = versions.get(new Version("1.12.6"));
        assertEquals(API_URL + "/repository/betonquest/org/betonquest/betonquest/1.12.6/betonquest-1.12.6-shaded.jar", url3,
                "The download URL is not correct");
    }

    @SuppressWarnings({"PMD.JUnitTestContainsTooManyAsserts"})
    @Test
    void testNexusDevelopementSource() throws IOException {
        final Path filePathPom1 = Path.of("src/test/resources/modules/web/updater/nexusPom1.xml");
        final Path filePathPom2 = Path.of("src/test/resources/modules/web/updater/nexusPom2.xml");
        final String apiUrlPom1 = API_URL + "/repository/betonquest/org/betonquest/betonquest/2.0.0-SNAPSHOT/betonquest-2.0.0-20221230.085132-398.pom";
        final String apiUrlPom2 = API_URL + "/repository/betonquest/org/betonquest/betonquest/1.12.7-SNAPSHOT/betonquest-1.12.7-20221210.125708-379.pom";

        final NexusReleaseAndDevelopmentSource source = getMockedNexusSource();
        doReturn(Files.readString(filePathPom1)).when(source).readStringFromURL(new URL(apiUrlPom1));
        doReturn(Files.readString(filePathPom2)).when(source).readStringFromURL(new URL(apiUrlPom2));
        final Map<Version, String> versions = source.getDevelopmentVersions();

        assertEquals(2, versions.size(), "Expected two versions from getReleaseVersions");
        final String url1 = versions.get(new Version("2.0.0-DEV-495"));
        assertEquals(apiUrlPom1.replace(".pom", "-shaded.jar"), url1, "The download URL is not correct");
        final String url2 = versions.get(new Version("1.12.7-DEV-379"));
        assertEquals(apiUrlPom2.replace(".pom", "-shaded.jar"), url2, "The download URL is not correct");
    }
}
