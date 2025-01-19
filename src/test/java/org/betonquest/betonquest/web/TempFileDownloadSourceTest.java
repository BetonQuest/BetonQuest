package org.betonquest.betonquest.web;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class tests the {@link TempFileDownloadSource}.
 */
class TempFileDownloadSourceTest {
    /**
     * The {@link File} to the BetonQuest.jar in the resources.
     */
    private static final File UPDATE_FILE = new File("src/test/resources/modules/web/updater/BetonQuest.jar");

    /**
     * The {@link Path} to the BetonQuest.jar in the resources.
     */
    private static final Path UPDATE_FILE_PATH = UPDATE_FILE.toPath();

    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    @Test
    void testDownloadFile(@TempDir final File tempDir) throws IOException {
        final File file = new File(tempDir, "BetonQuest.jar");
        final DownloadSource downloadSource = new TempFileDownloadSource((url, tempFile) -> {
            assertNotEquals(file, tempFile, "The temporary file is the same as the target file!");
            Files.copy(UPDATE_FILE_PATH, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        });

        downloadSource.get(UPDATE_FILE_PATH.toUri().toURL(), file);

        assertTrue(FileUtils.contentEquals(file, UPDATE_FILE),
                "The received file is not equal to the expected one!");
    }
}
