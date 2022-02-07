package org.betonquest.betonquest.modules.config;

import lombok.CustomLog;

import java.util.concurrent.Callable;

/**
 * Download files from any public GitHub repository and extract them to your QuestPackages folder.
 */
@CustomLog(topic = "Downloader")
public class Downloader implements Callable<Boolean> {

    /**
     * Directory where downloaded repositories should be cached
     */
    private static final String CACHE_DIR = "/.cache/downloader/";

    /**
     * Owner of the repository from which the files are to be downloaded
     */
    private final String owner;

    /**
     * Name of the repository from which the files are to be downloaded.
     * Must be a public repo.
     */
    private final String repo;

    /**
     * Git branch from which the files are to be downloaded
     */
    private final String branch;

    /**
     * A relative path in the remote repository specified by {@code owner} and {@code repo} fields.
     * Only files in this path should be extracted, all other files should remain cached.
     */
    private final String path;

    public Downloader(String owner, String repo, String branch, String path) {
        this.owner = owner;
        this.repo = repo;
        this.branch = branch;
        this.path = path;
    }


    //TODO should result give more details than just returning true?

    /**
     * Run the downloader with the specified settings
     *
     * @return result of the download, generally true
     * @throws Exception if any exception occurred during download
     */
    @Override
    public Boolean call() throws Exception {
        //TODO Implement
        throw new UnsupportedOperationException("todo implement");
    }
}
