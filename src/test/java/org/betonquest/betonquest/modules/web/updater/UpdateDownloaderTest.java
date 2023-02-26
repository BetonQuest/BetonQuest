package org.betonquest.betonquest.modules.web.updater;

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

/**
 * This class test the {@link UpdateDownloader}.
 */
class UpdateDownloaderTest {
    /**
     * The {@link File} to the BetonQuest.jar in the resources.
     */
    private static final File UPDATE_FILE = new File("src/test/resources/modules/web/updater/BetonQuest.jar");
    /**
     * The {@link Path} to the BetonQuest.jar in the resources.
     */
    private static final Path UPDATE_FILE_PATH = UPDATE_FILE.toPath();

    @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
    @Test
    void testDownloadFile(@TempDir final File tempDir) throws IOException, QuestRuntimeException {
        final File updateFolder = new File(tempDir, "updater");
        final File tempFile = new File(updateFolder, "BetonQuest.jar.temp");
        final File finalFile = new File(updateFolder, "BetonQuest.jar");

        final UpdateDownloader downloader = new UpdateDownloader(tempDir.toURI(), tempFile, finalFile);
        assertFalse(downloader.alreadyDownloaded(), "There should be no a BetonQuest.jar file");
        downloader.downloadToFile(getUrl());

        assertTrue(downloader.alreadyDownloaded(), "There should be a BetonQuest.jar file");
        assertTrue(FileUtils.contentEquals(finalFile, UPDATE_FILE),
                "The received file is not equal to the expected one!");
    }

    @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
    @Test
    void testDownloadFileAlreadyExists(@TempDir final File tempDir) throws IOException, QuestRuntimeException {
        final File updateFolder = new File(tempDir, "updater");
        final File tempFile = new File(updateFolder, "BetonQuest.jar.temp");
        final File finalFile = new File(updateFolder, "BetonQuest.jar");

        assertTrue(updateFolder.mkdirs(), "Expected successfully created the updater folder");
        assertTrue(finalFile.createNewFile(), "Expected successfully creation of a BetonQuest.jar");

        final UpdateDownloader downloader = new UpdateDownloader(tempDir.toURI(), tempFile, finalFile);
        assertTrue(downloader.alreadyDownloaded(), "There should be a BetonQuest.jar file");
        downloader.downloadToFile(getUrl());

        assertTrue(downloader.alreadyDownloaded(), "There should be a BetonQuest.jar file");
        assertTrue(FileUtils.contentEquals(finalFile, UPDATE_FILE),
                "The received file is not equal to the expected one!");
    }

    @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
    @Test
    void testExceptionFolderCreation(@TempDir final File tempDir) {
        final File updateFolder = spy(new File(tempDir, "updater"));
        final File tempFile = spy(new File(updateFolder, "BetonQuest.jar.temp"));
        final File finalFile = new File(updateFolder, "BetonQuest.jar");

        doReturn(false).when(updateFolder).mkdirs();
        doReturn(updateFolder).when(tempFile).getParentFile();

        final UpdateDownloader downloader = new UpdateDownloader(tempDir.toURI(), tempFile, finalFile);

        final QuestRuntimeException exception = assertThrows(QuestRuntimeException.class, () -> downloader.downloadToFile(mock(URL.class)), "Expected QuestRuntimeException");
        assertEquals("The updater could not create the folder 'updater'!", exception.getMessage(), "Expected exception message does not Match");
        assertFalse(downloader.alreadyDownloaded(), "There should be no a BetonQuest.jar file");
    }

    @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
    @Test
    void testExceptionTempFileCreation(@TempDir final File tempDir) throws IOException {
        final File updateFolder = new File(tempDir, "updater");
        final File tempFile = spy(new File(updateFolder, "BetonQuest.jar.temp"));
        final File finalFile = new File(updateFolder, "BetonQuest.jar");

        doThrow(new IOException("Test Exception")).when(tempFile).createNewFile();

        final UpdateDownloader downloader = new UpdateDownloader(tempDir.toURI(), tempFile, finalFile);

        final QuestRuntimeException exception = assertThrows(QuestRuntimeException.class, () -> downloader.downloadToFile(mock(URL.class)), "Expected QuestRuntimeException");
        assertEquals("The updater could not create the file 'updater/BetonQuest.jar.temp'! Reason: Test Exception", exception.getMessage(),
                "Expected exception message does not Match");
        assertFalse(downloader.alreadyDownloaded(), "There should be no a BetonQuest.jar file");
    }

    @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
    @Test
    void testExceptionTempFileAlreadyExists(@TempDir final File tempDir) throws IOException {
        final File updateFolder = new File(tempDir, "updater");
        final File tempFile = new File(updateFolder, "BetonQuest.jar.temp");
        final File finalFile = new File(updateFolder, "BetonQuest.jar");

        assertTrue(updateFolder.mkdirs(), "Expected successfully created the updater folder");
        assertTrue(tempFile.createNewFile(), "Expected successfully creation of a BetonQuest.jar");

        final UpdateDownloader downloader = new UpdateDownloader(tempDir.toURI(), tempFile, finalFile);

        final QuestRuntimeException exception = assertThrows(QuestRuntimeException.class, () -> downloader.downloadToFile(mock(URL.class)), "Expected QuestRuntimeException");
        assertEquals("The file 'updater/BetonQuest.jar.temp' already exists! Please wait for the active download to finish. If there is no active download delete the file manually.", exception.getMessage(),
                "Expected exception message does not Match");
        assertFalse(downloader.alreadyDownloaded(), "There should be no a BetonQuest.jar file");
    }

    @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
    @Test
    void testExceptionTempFileIOException(@TempDir final File tempDir) throws IOException {
        final File updateFolder = new File(tempDir, "updater");
        final File tempFile = new File(updateFolder, "BetonQuest.jar.temp");
        final File finalFile = new File(updateFolder, "BetonQuest.jar");

        final UpdateDownloader downloader = new UpdateDownloader(tempDir.toURI(), tempFile, finalFile);
        final URL url = mock(URL.class);
        when(url.openConnection()).thenThrow(new IOException("Test Exception"));

        final QuestRuntimeException exception = assertThrows(QuestRuntimeException.class, () -> downloader.downloadToFile(url), "Expected QuestRuntimeException");
        assertEquals("The download was interrupted! The updater could not download the file! You can try if again, if it still does not work use a manual download. The original exception was: Test Exception", exception.getMessage(),
                "Expected exception message does not Match");
        assertFalse(downloader.alreadyDownloaded(), "There should be no a BetonQuest.jar file");
    }

    @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
    @Test
    void testExceptionTempFileNotDeletableIOException(@TempDir final File tempDir) throws IOException {
        final File updateFolder = new File(tempDir, "updater");
        final File tempFile = spy(new File(updateFolder, "BetonQuest.jar.temp"));
        final File finalFile = new File(updateFolder, "BetonQuest.jar");

        doReturn(false).when(tempFile).delete();

        final UpdateDownloader downloader = new UpdateDownloader(tempDir.toURI(), tempFile, finalFile);
        final URL url = mock(URL.class);
        when(url.openConnection()).thenThrow(new IOException("Test Exception"));

        final QuestRuntimeException exception = assertThrows(QuestRuntimeException.class, () -> downloader.downloadToFile(url), "Expected QuestRuntimeException");
        assertEquals("The download was interrupted! There is a broken file at 'updater/BetonQuest.jar.temp'. Delete this file otherwise a new download is not possible. The original exception was: Test Exception", exception.getMessage(),
                "Expected exception message does not Match");
        assertFalse(downloader.alreadyDownloaded(), "There should be no a BetonQuest.jar file");
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
