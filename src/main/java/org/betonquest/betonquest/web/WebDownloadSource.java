package org.betonquest.betonquest.web;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * This is an implementation of the {@link DownloadSource} interface that downloads a file from a given {@link URL}.
 */
public class WebDownloadSource implements DownloadSource {

    /**
     * Constructs a new {@link WebDownloadSource}.
     */
    public WebDownloadSource() {
    }

    @Override
    public void get(final URL url, final File file) throws IOException {
        FileUtils.copyURLToFile(url, file, 5000, 5000);
    }
}
