package org.betonquest.betonquest.web.updater;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.web.DownloadSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * This class tests the {@link UpdateDownloader}.
 */
class UpdateDownloaderTest {

    @Test
    void testDownloadFile(@TempDir final File tempDir) throws IOException, QuestException {
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

        final QuestException exception = assertThrows(QuestException.class, () -> downloader.downloadToFile(url), "Expected QuestException");
        assertEquals("The download was interrupted! The updater could not download the file! You can try it again, if it still does not work use a manual download. The original exception was: Test Exception", exception.getMessage(), "Expected exception message does not Match");
    }

    @Test
    void testExceptionOnFolderCreation(@TempDir final File tempDir) {
        final File updateFolder = spy(new File(tempDir, "updater"));
        final File file = spy(new File(updateFolder, "BetonQuest.jar"));
        final DownloadSource downloadSource = mock(DownloadSource.class);
        final UpdateDownloader downloader = new UpdateDownloader(downloadSource, file);

        doReturn(updateFolder).when(file).getParentFile();
        doReturn(false).when(updateFolder).mkdirs();

        final QuestException exception = assertThrows(QuestException.class, () -> downloader.downloadToFile(mock(URL.class)), "Expected QuestException");
        assertEquals("The updater could not create the folder '" + updateFolder.getAbsolutePath() + "'!", exception.getMessage(), "Expected exception message does not Match");
    }

    @Test
    @SuppressWarnings("PMD.DoNotUseThreads")
    void testConcurrentDownloads(@TempDir final File tempDir) throws IOException, InterruptedException {
        @SuppressWarnings("PMD.CloseResource") final ExecutorService service = Executors.newFixedThreadPool(1);

        final File file = new File(tempDir, "BetonQuest.jar");
        final URL url = mock(URL.class);
        final DownloadSource downloadSource = mock(DownloadSource.class);
        final UpdateDownloader downloader = new UpdateDownloader(downloadSource, file);

        final Semaphore semaphore = new Semaphore(0);
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        doAnswer(invocation -> {
            countDownLatch.countDown();
            semaphore.acquire();
            return null;
        }).when(downloadSource).get(url, file);
        try {
            service.execute(() -> {
                try {
                    downloader.downloadToFile(url);
                } catch (final QuestException e) {
                    throw new IllegalStateException(e);
                }
            });
            countDownLatch.await();
            final QuestException exception = assertThrows(QuestException.class, () -> downloader.downloadToFile(url), "Expected QuestException");
            assertEquals("The updater is already downloading the update! Please wait until it is finished!", exception.getMessage(), "Expected exception message does not Match");
        } finally {
            semaphore.release();
            service.shutdown();
        }
    }
}
