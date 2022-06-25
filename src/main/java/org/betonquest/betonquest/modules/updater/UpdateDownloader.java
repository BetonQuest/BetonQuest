package org.betonquest.betonquest.modules.updater;

import org.apache.commons.io.FileUtils;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * Bukkit has a 'update' (default) folder, where jar's that have the exact same name as a jar in the 'plugins' folder,
 * will be moved to the `plugins` folder on server startup.
 * This downloader helps to download a jar from a given {@link URL} that is first saved as `NAME.tmp` file and
 * then renamed to `NAME` when the download was successful.
 */
public class UpdateDownloader {
    /**
     * The name of the jar file in the `plugins` folder
     */
    private final String fileName;
    /**
     * The folder of the `update` (default) folder
     */
    private final File updateFolder;

    /**
     * Creates a new {@link UpdateDownloader} with the given properties.
     *
     * @param fileName     The name of the jar file in the `plugins` folder
     * @param updateFolder The folder of the `update` (default) folder
     */
    public UpdateDownloader(final String fileName, final File updateFolder) {
        this.fileName = fileName;
        this.updateFolder = updateFolder;
    }

    /**
     * Downloads a given URL to the chosen file from the constructor.
     *
     * @param url The {@link URL} where to download this jar from
     * @throws QuestRuntimeException Is thrown if there was any exception during the download process.
     */
    public void downloadToFile(final URL url) throws QuestRuntimeException {
        checkAndCreateFolder();
        final File tmpFile = new File(updateFolder, fileName + ".tmp");
        tmpFile.deleteOnExit();

        try {
            checkAndCreateFile(tmpFile);
            downloadToFileFromURL(url, tmpFile);
            renameFile(tmpFile, new File(updateFolder, fileName));
        } catch (final IOException e) {
            if (tmpFile.exists() && !tmpFile.delete()) {
                throw new QuestRuntimeException("Download was interrupted! There is a broken file '"
                        + tmpFile.getAbsolutePath() + "'."
                        + " Delete this file otherwise a new download is not possible."
                        + " If it still does not work use a manual download.", e);
            }
            throw new QuestRuntimeException("The updater could not download the file, try it again!"
                    + " If it still does not work use a manual download.", e);
        }
    }

    private void renameFile(final File tmpFile, final File targetFile) throws QuestRuntimeException {
        try {
            Files.move(tmpFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (final IOException e) {
            throw new QuestRuntimeException("Could not rename the downloaded file, try it again!"
                    + " If it still does not work use a manual download.", e);
        }
    }

    private void checkAndCreateFile(final File tmpFile) throws QuestRuntimeException, IOException {
        if (tmpFile.exists()) {
            throw new QuestRuntimeException("The file '" + tmpFile.getName() + "' already exists!" +
                    " Please wait for the currently running update to finish. If no update is running delete the file manually.");
        }
        if (!tmpFile.createNewFile()) {
            throw new QuestRuntimeException("The updater could not create the file '" + tmpFile.getName() + "'!");
        }
    }

    private void checkAndCreateFolder() throws QuestRuntimeException {
        if (!updateFolder.exists() && !updateFolder.mkdirs()) {
            throw new QuestRuntimeException("The updater could not create the folder '" + updateFolder.getName() + "'!");
        }
    }

    private void downloadToFileFromURL(final URL url, final File file) throws IOException {
        FileUtils.copyURLToFile(url, file, 5000, 5000);
    }
}
