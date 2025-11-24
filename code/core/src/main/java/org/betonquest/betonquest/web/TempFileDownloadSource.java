package org.betonquest.betonquest.web;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * This is an implementation of the {@link DownloadSource} interface that downloads a file from a given {@link URL}
 * to a temporary file and then moves it to the given {@link File}.
 */
public class TempFileDownloadSource implements DownloadSource {

    /**
     * The {@link DownloadSource} to use.
     */
    private final DownloadSource downloadSource;

    /**
     * Constructs a new {@link TempFileDownloadSource}.
     *
     * @param downloadSource the {@link DownloadSource} to use
     */
    public TempFileDownloadSource(final DownloadSource downloadSource) {
        this.downloadSource = downloadSource;
    }

    @Override
    public void get(final URL url, final File file) throws IOException {
        final File tempFile = File.createTempFile(file.getName() + "-", ".tmp");
        tempFile.deleteOnExit();
        downloadSource.get(url, tempFile);
        try {
            Files.move(tempFile.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (final IOException e) {
            throw new IOException("Could not move temporary file to target file '" + file.getAbsolutePath() + "'", e);
        }
    }
}
