package org.betonquest.betonquest.web.updater;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.version.Version;
import org.betonquest.betonquest.web.DownloadSource;
import org.bukkit.plugin.PluginDescriptionFile;

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
     * The folder where the update jar will be saved.
     */
    private final File updateFolder;

    /**
     * The plugin description to get the plugin name from.
     */
    private final PluginDescriptionFile pluginDescriptionFile;

    /**
     * A flag to check if the download is currently running.
     */
    private final AtomicBoolean currentlyDownloading;

    /**
     * Creates a new {@link UpdateDownloader} with the given file locations.
     *
     * @param downloadSource        The {@link DownloadSource} to use.
     * @param updateFolder          The folder where the update jar will be saved.
     * @param pluginDescriptionFile The plugin description to get the plugin name from.
     */
    public UpdateDownloader(final DownloadSource downloadSource, final File updateFolder,
                            final PluginDescriptionFile pluginDescriptionFile) {
        this.downloadSource = downloadSource;
        this.updateFolder = updateFolder;
        this.pluginDescriptionFile = pluginDescriptionFile;
        this.currentlyDownloading = new AtomicBoolean(false);
    }

    /**
     * Downloads a given URL to the chosen file from the constructor.
     *
     * @param version The version of the update to download
     * @param url     The {@link URL} where to download this jar from
     * @throws QuestException Is thrown if there was any exception during the download process.
     */
    public void downloadToFile(final Version version, final URL url) throws QuestException {
        checkAndCreateFolder(updateFolder);
        try {
            final boolean runningDownload = currentlyDownloading.compareAndSet(false, true);
            if (!runningDownload) {
                throw new QuestException("The updater is already downloading the update! Please wait until it is finished!");
            }
            downloadSource.get(url, getUpdateFile(version));
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
     * @param version The version of the update to check
     * @return true if a successful download was already done
     */
    public boolean alreadyDownloaded(final Version version) {
        return getUpdateFile(version).exists();
    }

    private void checkAndCreateFolder(final File file) throws QuestException {
        if (!file.exists() && !file.mkdirs()) {
            throw new QuestException("The updater could not create the folder '" + file.getAbsolutePath() + "'!");
        }
    }

    private File getUpdateFile(final Version version) {
        return new File(updateFolder, pluginDescriptionFile.getName() + "-" + version + ".jar");
    }
}
