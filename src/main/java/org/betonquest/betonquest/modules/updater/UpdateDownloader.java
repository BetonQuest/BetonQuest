package org.betonquest.betonquest.modules.updater;

import org.apache.commons.io.FileUtils;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * Bukkit has a 'update' (default) folder, where jars that have the exact same name as a jar in the 'plugins' folder,
 * will be moved to the `plugins` folder on server startup.
 * This downloader helps to download a jar from a given {@link URL} that is first saved as temporary file and
 * then renamed to the final name when the download was successful.
 */
public class UpdateDownloader {

    /**
     * The root of the files for better path description in exceptions.
     */
    private final URI relativeRoot;
    /**
     * A {@link File} where to create the temporary file for the download.
     */
    private final File tempFile;
    /**
     * The final {@link File} for the download.
     */
    private final File finalFile;

    /**
     * Creates a new {@link UpdateDownloader} with the given file locations.
     *
     * @param relativeRoot The root of the files for better path description in exceptions.
     * @param tempFile     A {@link File} where to create the temporary file for the download.
     * @param finalFile    The final {@link File} for the download.
     */
    public UpdateDownloader(final URI relativeRoot, final File tempFile, final File finalFile) {
        this.relativeRoot = relativeRoot;
        this.tempFile = tempFile;
        this.finalFile = finalFile;
    }

    /**
     * Downloads a given URL to the chosen file from the constructor.
     *
     * @param url The {@link URL} where to download this jar from
     * @throws QuestRuntimeException Is thrown if there was any exception during the download process.
     */
    public void downloadToFile(final URL url) throws QuestRuntimeException {
        checkAndCreateFolder(tempFile.getParentFile());
        checkAndCreateFolder(finalFile.getParentFile());
        tempFile.deleteOnExit();

        checkAndCreateFile();
        try {
            downloadToFileFromURL(url, tempFile);
            Files.move(tempFile.toPath(), finalFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (final IOException e) {
            final String prefix = "The download was interrupted! ";
            final String suffix = " The original exception was: " + e.getMessage();
            if (tempFile.exists() && !tempFile.delete()) {
                throw new QuestRuntimeException(prefix + "There is a broken file at '" + getRelativePath(tempFile)
                        + "'. Delete this file otherwise a new download is not possible." + suffix, e);
            }
            throw new QuestRuntimeException(prefix + "The updater could not download the file!"
                    + " You can try if again, if it still does not work use a manual download." + suffix, e);
        }
    }

    /**
     * Checks if the final {@link File} was already downloaded, by checking for its existence.
     *
     * @return true if a successful download was already done
     */
    public boolean alreadyDownloaded() {
        return finalFile.exists();
    }

    private void checkAndCreateFile() throws QuestRuntimeException {
        try {
            if (!tempFile.createNewFile()) {
                throw new QuestRuntimeException("The file '" + getRelativePath(tempFile) + "' already exists!"
                        + " Please wait for the active download to finish."
                        + " If there is no active download delete the file manually.");
            }
        } catch (final IOException e) {
            throw new QuestRuntimeException("The updater could not create the file '" + getRelativePath(tempFile)
                    + "'! Reason: " + e.getMessage(), e);
        }
    }

    private void checkAndCreateFolder(final File file) throws QuestRuntimeException {
        if (!file.exists() && !file.mkdirs()) {
            throw new QuestRuntimeException("The updater could not create the folder '" + getRelativePath(file) + "'!");
        }
    }

    private void downloadToFileFromURL(final URL url, final File file) throws IOException {
        FileUtils.copyURLToFile(url, file, 5000, 5000);
    }

    private String getRelativePath(final File file) {
        return relativeRoot.relativize(file.toURI()).getPath();
    }
}
