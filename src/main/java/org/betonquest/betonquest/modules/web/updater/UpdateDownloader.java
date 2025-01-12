package org.betonquest.betonquest.modules.web.updater;

import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.modules.web.DownloadSource;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Bukkit has an 'update' (default) folder, where jars that have the exact same name as a jar in the 'plugins' folder,
 * will be moved to the `plugins` folder on server startup.
 * This downloader helps to download a jar from a given {@link URL} that is first saved as a temporary file and
 * then renamed to the final name when the download was successful.
 */
public class UpdateDownloader {

    /**
     * The {@link DownloadSource} to use.
     */
    private final DownloadSource downloadSource;

    /**
     * The final {@link File} for the download.
     */
    private final File file;

    /**
     * A flag to check if the download is currently running.
     */
    private final AtomicBoolean currentlyDownloading;

    /**
     * Creates a new {@link UpdateDownloader} with the given file locations.
     *
     * @param downloadSource The {@link DownloadSource} to use.
     * @param file           The final {@link File} for the download.
     */
    public UpdateDownloader(final DownloadSource downloadSource, final File file) {
        this.downloadSource = downloadSource;
        this.file = file;
        this.currentlyDownloading = new AtomicBoolean(false);
    }

    /**
     * Downloads a given URL to the chosen file from the constructor.
     *
     * @param url The {@link URL} where to download this jar from
     * @throws QuestException Is thrown if there was any exception during the download process.
     */
    public void downloadToFile(final URL url) throws QuestException {
        checkAndCreateFolder(file.getParentFile());
        try {
            final boolean runningDownload = currentlyDownloading.compareAndSet(false, true);
            if (!runningDownload) {
                throw new QuestException("The updater is already downloading the update! Please wait until it is finished!");
            }
            downloadSource.get(url, file);
        } catch (final IOException e) {
            throw new QuestException("The download was interrupted! The updater could not download the file!"
                    + " You can try it again, if it still does not work use a manual download."
                    + " The original exception was: " + e.getMessage(), e);
        } finally {
            currentlyDownloading.set(false);
        }
    }

    /**
     * Checks if the final {@link File} was already downloaded, by checking for its existence.
     *
     * @return true if a successful download was already done
     */
    public boolean alreadyDownloaded() {
        return file.exists();
    }

    private void checkAndCreateFolder(final File file) throws QuestException {
        if (!file.exists() && !file.mkdirs()) {
            throw new QuestException("The updater could not create the folder '" + file.getAbsolutePath() + "'!");
        }
    }
}
