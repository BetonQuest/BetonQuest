package org.betonquest.betonquest.modules.web.updater;

import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.modules.web.DownloadSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * This class test the {@link UpdateDownloader}.
 */
class UpdateDownloaderTest {

    @Test
    void testDownloadFile(@TempDir final File tempDir) throws IOException, QuestRuntimeException {
        final File file = new File(tempDir, "BetonQuest.jar");
        final URL url = mock(URL.class);
        final DownloadSource downloadSource = mock(DownloadSource.class);
        final UpdateDownloader downloader = new UpdateDownloader(downloadSource, file);

        downloader.downloadToFile(url);
        verify(downloadSource, times(1)).get(url, file);
    }

    @Test
    void testDownloadFileAlreadyExists(@TempDir final File tempDir) throws IOException {
        final File file = new File(tempDir, "BetonQuest.jar");
        final DownloadSource downloadSource = mock(DownloadSource.class);
        final UpdateDownloader downloader = new UpdateDownloader(downloadSource, file);

        assertFalse(downloader.alreadyDownloaded(), "There shouldn't be a BetonQuest.jar file");
        Files.createFile(file.toPath());
        assertTrue(downloader.alreadyDownloaded(), "There should be a BetonQuest.jar file");
    }

    @Test
    void testExceptionOnDownload(@TempDir final File tempDir) throws IOException {
        final File file = new File(tempDir, "BetonQuest.jar");
        final URL url = mock(URL.class);
        final DownloadSource downloadSource = mock(DownloadSource.class);
        doThrow(new IOException("Test Exception")).when(downloadSource).get(url, file);
        final UpdateDownloader downloader = new UpdateDownloader(downloadSource, file);

        final QuestRuntimeException exception = assertThrows(QuestRuntimeException.class, () -> downloader.downloadToFile(url), "Expected QuestRuntimeException");
        assertEquals("The download was interrupted! The updater could not download the file! You can try if again, if it still does not work use a manual download. The original exception was: Test Exception", exception.getMessage(), "Expected exception message does not Match");
    }

    @Test
    void testExceptionOnFolderCreation(@TempDir final File tempDir) {
        final File updateFolder = spy(new File(tempDir, "updater"));
        final File file = spy(new File(updateFolder, "BetonQuest.jar"));
        final DownloadSource downloadSource = mock(DownloadSource.class);
        final UpdateDownloader downloader = new UpdateDownloader(downloadSource, file);

        doReturn(updateFolder).when(file).getParentFile();
        doReturn(false).when(updateFolder).mkdirs();

        final QuestRuntimeException exception = assertThrows(QuestRuntimeException.class, () -> downloader.downloadToFile(mock(URL.class)), "Expected QuestRuntimeException");
        assertEquals("The updater could not create the folder '" + updateFolder.getAbsolutePath() + "'!", exception.getMessage(), "Expected exception message does not Match");
    }

    @Test
    @SuppressWarnings("PMD.DoNotUseThreads")
    void testConcurrentDownloads(@TempDir final File tempDir) throws IOException {
        final ExecutorService service = Executors.newFixedThreadPool(1);

        final File file = new File(tempDir, "BetonQuest.jar");
        final URL url = mock(URL.class);
        final DownloadSource downloadSource = mock(DownloadSource.class);
        final UpdateDownloader downloader = new UpdateDownloader(downloadSource, file);

        final Semaphore semaphore = new Semaphore(0);
        doAnswer(invocation -> {
            semaphore.acquire();
            return null;
        }).when(downloadSource).get(url, file);
        service.execute(() -> {
            try {
                downloader.downloadToFile(url);
            } catch (final QuestRuntimeException e) {
                throw new IllegalStateException(e);
            }
        });
        final QuestRuntimeException exception = assertThrows(QuestRuntimeException.class, () -> downloader.downloadToFile(url), "Expected QuestRuntimeException");
        assertEquals("The updater is already downloading the update! Please wait until it is finished!", exception.getMessage(), "Expected exception message does not Match");
        semaphore.release();
        service.shutdown();
    }
}
