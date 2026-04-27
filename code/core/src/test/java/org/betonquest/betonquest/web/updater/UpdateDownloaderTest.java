package org.betonquest.betonquest.web.updater;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.version.Version;
import org.betonquest.betonquest.lib.version.BetonQuestVersion;
import org.betonquest.betonquest.web.DownloadSource;
import org.bukkit.plugin.PluginDescriptionFile;
import org.junit.jupiter.api.BeforeAll;
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

    /**
     * The plugin description file used for testing.
     */
    private static final PluginDescriptionFile PLUGIN_DESCRIPTION_FILE = mock(PluginDescriptionFile.class);

    /**
     * The version used for testing.
     */
    private static final Version VERSION = BetonQuestVersion.parse("1.2.3");

    @BeforeAll
    static void setup() {
        when(PLUGIN_DESCRIPTION_FILE.getName()).thenReturn("BetonQuest");
    }

    @Test
    void testDownloadFile(@TempDir final File tempDir) throws IOException, QuestException {
        final File file = new File(tempDir, "BetonQuest-1.2.3.jar");
        final URL url = mock(URL.class);
        final DownloadSource downloadSource = mock(DownloadSource.class);
        final UpdateDownloader downloader = new UpdateDownloader(downloadSource, tempDir, PLUGIN_DESCRIPTION_FILE);

        downloader.downloadToFile(VERSION, url);
        verify(downloadSource, times(1)).get(url, file);
    }

    @Test
    void testDownloadFileAlreadyExists(@TempDir final File tempDir) throws IOException {
        final File file = new File(tempDir, "BetonQuest-1.2.3.jar");
        final DownloadSource downloadSource = mock(DownloadSource.class);
        final UpdateDownloader downloader = new UpdateDownloader(downloadSource, tempDir, PLUGIN_DESCRIPTION_FILE);

        assertFalse(downloader.alreadyDownloaded(VERSION), "There shouldn't be a BetonQuest.jar file");
        Files.createFile(file.toPath());
        assertTrue(downloader.alreadyDownloaded(VERSION), "There should be a BetonQuest.jar file");
    }

    @Test
    void testExceptionOnDownload(@TempDir final File tempDir) throws IOException {
        final File file = new File(tempDir, "BetonQuest-1.2.3.jar");
        final URL url = mock(URL.class);
        final DownloadSource downloadSource = mock(DownloadSource.class);
        doThrow(new IOException("Test Exception")).when(downloadSource).get(url, file);
        final UpdateDownloader downloader = new UpdateDownloader(downloadSource, tempDir, PLUGIN_DESCRIPTION_FILE);

        final QuestException exception = assertThrows(QuestException.class, () -> downloader.downloadToFile(VERSION, url), "Expected QuestException");
        assertEquals("The download was interrupted! The updater could not download the file! You can try it again, if it still does not work use a manual download. The original exception was: Test Exception", exception.getMessage(), "Expected exception message does not Match");
    }

    @Test
    void testExceptionOnFolderCreation(@TempDir final File tempDir) {
        final File updateFolder = spy(new File(tempDir, "updater"));
        final DownloadSource downloadSource = mock(DownloadSource.class);
        final UpdateDownloader downloader = new UpdateDownloader(downloadSource, updateFolder, PLUGIN_DESCRIPTION_FILE);

        doReturn(false).when(updateFolder).mkdirs();

        final QuestException exception = assertThrows(QuestException.class, () -> downloader.downloadToFile(mock(Version.class), mock(URL.class)), "Expected QuestException");
        assertEquals("The updater could not create the folder '" + updateFolder.getAbsolutePath() + "'!", exception.getMessage(), "Expected exception message does not Match");
    }

    @Test
    // TODO version switch:
    //  Remove suppression PMD.CloseResource when only Java 21 is supported
    @SuppressWarnings({"PMD.DoNotUseThreads", "PMD.CloseResource"})
    void testConcurrentDownloads(@TempDir final File tempDir) throws IOException, InterruptedException {
        final ExecutorService service = Executors.newFixedThreadPool(1);

        final URL url = mock(URL.class);
        final DownloadSource downloadSource = mock(DownloadSource.class);
        final UpdateDownloader downloader = new UpdateDownloader(downloadSource, tempDir, PLUGIN_DESCRIPTION_FILE);

        final Semaphore semaphore = new Semaphore(0);
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        doAnswer(invocation -> {
            countDownLatch.countDown();
            semaphore.acquire();
            return null;
        }).when(downloadSource).get(any(), any());
        try {
            service.execute(() -> {
                try {
                    downloader.downloadToFile(VERSION, url);
                } catch (final QuestException e) {
                    throw new IllegalStateException(e);
                }
            });
            countDownLatch.await();
            final QuestException exception = assertThrows(QuestException.class, () -> downloader.downloadToFile(VERSION, url), "Expected QuestException");
            assertEquals("The updater is already downloading the update! Please wait until it is finished!", exception.getMessage(), "Expected exception message does not Match");
        } finally {
            semaphore.release();
            service.shutdown();
        }
    }
}
