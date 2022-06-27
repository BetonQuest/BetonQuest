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
 * This class test the {@link BetonQuestDevelopmentSourceTest}.
 */
class BetonQuestDevelopmentSourceTest {

    @SuppressWarnings({"PMD.JUnitTestContainsTooManyAsserts"})
    @Test
    void testBetonQuestDevSource() throws IOException {
        final Path filePath = Path.of("src/test/resources/modules/updater/latest.json");
        final String apiUrl = "https://betonquest.org";

        final BetonQuestDevelopmentSource devSource = spy(new BetonQuestDevelopmentSource(apiUrl));
        doReturn(Files.readString(filePath)).when(devSource)
                .readStringFromURL(new URL(apiUrl + BetonQuestDevelopmentSource.DEV_API_LATEST));

        final Map<Version, String> versions = devSource.getDevelopmentVersions();

        assertEquals(2, versions.size(), "Expected two versions from getReleaseVersions");
        final String url1 = versions.get(new Version("1.12.0-DEV-410"));
        assertEquals(apiUrl + "/builds/download/1.12.0/410/BetonQuest.jar", url1, "The download URL is not correct");
        final String url2 = versions.get(new Version("1.12.1-DEV-3"));
        assertEquals(apiUrl + "/builds/download/1.12.1/3/BetonQuest.jar", url2, "The download URL is not correct");
    }

}
