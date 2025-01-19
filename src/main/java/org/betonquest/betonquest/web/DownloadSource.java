package org.betonquest.betonquest.web;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * This is an interface that provides basic methods to read information from a given {@link URL}
 * and saves it to a given {@link File}.
 */
public interface DownloadSource {

    /**
     * This method can download a file from a given {@link URL}.
     *
     * @param url  that should be downloaded to the given {@link File}
     * @param file the {@link File} to download to
     * @throws IOException is thrown if any problem occurred while downloading the file from the {@link URL}
     */
    void get(URL url, File file) throws IOException;
}
