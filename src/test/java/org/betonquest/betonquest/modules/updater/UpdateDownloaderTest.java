package org.betonquest.betonquest.modules.updater;

import org.apache.commons.io.FileUtils;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UpdateDownloaderTest {
    private static final File UPDATE_FILE = new File("src/test/resources/modules/updater/BetonQuest.jar");
    private static final Path UPDATE_FILE_PATH = UPDATE_FILE.toPath();

    @Test
    void testDownload(@TempDir final File tempDir) throws IOException, QuestRuntimeException {
        final File updateFolder = new File(tempDir, "updater");
        final UpdateDownloader downloader = new UpdateDownloader("BetonQuest.jar", updateFolder);

        final URL url = getUrl();

        downloader.downloadToFile(url);

        final File betonQuestJar = new File(updateFolder, "BetonQuest.jar");
        assertTrue(betonQuestJar.exists(), "There should be a BetonQuest.jar file");
        assertTrue(FileUtils.contentEquals(betonQuestJar, UPDATE_FILE),
                "The received file is not equal to the expected one!");
    }

    @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
    @Test
    void testUpdateFolderCreation(@TempDir final File tempDir) throws IOException {
        final File updateFolder = spy(new File(tempDir, "updater"));
        doReturn(false).when(updateFolder).mkdirs();

        final UpdateDownloader downloader = new UpdateDownloader("BetonQuest.jar", updateFolder);
        final URL url = mock(URL.class);

        final QuestRuntimeException exception = assertThrows(QuestRuntimeException.class, () -> downloader.downloadToFile(url), "Expected QuestRuntimeException");
        assertEquals("The updater could not create the folder 'updater'!", exception.getMessage(), "Expected exception message does not Match");
        assertFalse(new File(updateFolder, "BetonQuest.jar").exists(), "There should be no a BetonQuest.jar file");
    }

    @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
    @Test
    void testUpdateFileAlreadyExistsIOException(@TempDir final File tempDir) throws IOException {
        final File updateFolder = new File(tempDir, "updater");
        assertTrue(updateFolder.createNewFile(), "Expected successfully creation of the updater folder");

        final UpdateDownloader downloader = new UpdateDownloader("BetonQuest.jar", updateFolder);
        final URL url = mock(URL.class);

        final QuestRuntimeException exception = assertThrows(QuestRuntimeException.class, () -> downloader.downloadToFile(url), "Expected QuestRuntimeException");
        assertEquals("The updater could not download the file, try it again! If it still does not work use a manual download.", exception.getMessage(),
                "Expected exception message does not Match");
        assertFalse(new File(updateFolder, "BetonQuest.jar").exists(), "There should be no a BetonQuest.jar file");
    }

    @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
    @Test
    void testTempUpdateFileAlreadyExists(@TempDir final File tempDir) throws IOException {
        final File updateFolder = new File(tempDir, "updater");
        assertTrue(updateFolder.mkdirs(), "Expected successfully created the updater folder");
        final File tmpFile = new File(updateFolder, "BetonQuest.jar.tmp");
        assertTrue(tmpFile.createNewFile(), "Expected successfully creation of a BetonQuest.jar");

        final UpdateDownloader downloader = new UpdateDownloader("BetonQuest.jar", updateFolder);
        final URL url = mock(URL.class);

        final QuestRuntimeException exception = assertThrows(QuestRuntimeException.class, () -> downloader.downloadToFile(url), "Expected QuestRuntimeException");
        assertEquals("The file 'BetonQuest.jar.tmp' already exists! Please wait for the currently running update to finish. If no update is running delete the file manually.", exception.getMessage(),
                "Expected exception message does not Match");
        assertFalse(new File(updateFolder, "BetonQuest.jar").exists(), "There should be no a BetonQuest.jar file");
    }

    @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
    @Test
    void testUpdateFileAlreadyExists(@TempDir final File tempDir) throws IOException, QuestRuntimeException {
        final File updateFolder = new File(tempDir, "updater");
        assertTrue(updateFolder.mkdirs(), "Expected successfully created the updater folder");
        final File tmpFile = new File(updateFolder, "BetonQuest.jar");
        assertTrue(tmpFile.createNewFile(), "Expected successfully creation of a BetonQuest.jar");

        final UpdateDownloader downloader = new UpdateDownloader("BetonQuest.jar", updateFolder);

        final URL url = getUrl();

        downloader.downloadToFile(url);

        final File betonQuestJar = new File(updateFolder, "BetonQuest.jar");
        assertTrue(betonQuestJar.exists(), "There should be a BetonQuest.jar file");
        assertTrue(FileUtils.contentEquals(betonQuestJar, UPDATE_FILE),
                "The received file is not equal to the expected one!");
    }

    @NotNull
    private URL getUrl() throws IOException {
        final URL url = mock(URL.class);
        final HttpURLConnection httpURLConnection = mock(HttpURLConnection.class);
        when(httpURLConnection.getInputStream()).thenReturn(Files.newInputStream(UPDATE_FILE_PATH));
        when(url.openConnection()).thenReturn(httpURLConnection);
        return url;
    }
}
