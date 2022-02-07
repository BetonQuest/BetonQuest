package org.betonquest.betonquest.modules.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import lombok.CustomLog;
import org.betonquest.betonquest.BetonQuest;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.Callable;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.StandardOpenOption.CREATE_NEW;

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
     * Base URL of the GitHub branches RestAPI.
     * Owner, repo and branch must be replaced with actual values.
     */
    private static final String GITHUB_BRANCHES_URL = "https://api.github.com/repos/{owner}/{repo}/branches/{branch}";


    /**
     * Base url where the files can be downloaded.
     * Owner, repo and commit sha must be replaced with actual values
     */
    private static final String GITHUB_DOWNLOAD_URL = "https://github.com/{owner}/{repo}/archive/{sha}.zip";

    /**
     * The response code 403.
     */
    public static final int RESPONSE_403 = 403;

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

    /**
     * SHA Hash of the branches latest commit.
     * Is null before {@link #requestCommitSHA()} has been called.
     */
    private String sha;

    /**
     * Construct a new downloader instance for the given repository and branch.
     * Call {@link #call()} to actually start the download
     *
     * @param owner  the owner of the repo
     * @param repo   the name of the repo
     * @param branch the name of the branch
     * @param path   path relative to repository root from which the files should be extracted
     */
    public Downloader(final String owner, final String repo, final String branch, final String path) {
        this.owner = owner;
        this.repo = repo;
        this.branch = branch;
        this.path = path;
    }


    //TODO should result give more details than just returning true?

    //TODO Refined exception handling

    //TODO cleanup old cache files

    /**
     * Run the downloader with the specified settings
     *
     * @return result of the download, generally true
     * @throws Exception if any exception occurred during download
     */
    @Override
    public Boolean call() throws Exception {
        requestCommitSHA();
        if (!cacheFile().exists()) {
            download();
        }
        extract();
        return true;
    }

    /**
     * Performs a get request to the GitHub RestAPI to retrieve the SHA hash of the latest commit on the branch.
     *
     * @throws IOException if any io error occurs while during request or parsing
     */
    private void requestCommitSHA() throws IOException {
        final URL url = new URL(GITHUB_BRANCHES_URL
                .replace("{owner}", owner)
                .replace("{repo}", repo)
                .replace("{branch}", branch));
        final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.connect();
        final int code = connection.getResponseCode();
        if (code == RESPONSE_403) {
            throw new IOException("It looks like too many requests were made to the github api, please wait until you have been unblocked.");
        }
        try (InputStreamReader reader = new InputStreamReader(connection.getInputStream(), UTF_8)) {
            final Optional<JsonElement> commit = Optional.ofNullable(JsonParser.parseReader(reader).getAsJsonObject().get("commit"));
            final Optional<JsonElement> sha = Optional.ofNullable(commit.orElseThrow().getAsJsonObject().get("sha"));
            this.sha = sha.orElseThrow().getAsString();
        } catch (JsonParseException | NoSuchElementException | IllegalStateException e) {
            throw new IOException("Unable to parse the JSON returned by Github API", e);
        }
    }

    /**
     * The file inside the cache directory where the repo is cached
     *
     * @return zip file containing the repo data
     */
    private File cacheFile() {
        final String filename = CACHE_DIR + owner + "_" + repo + "_" + sha.substring(0, 7) + ".zip";
        return new File(BetonQuest.getInstance().getDataFolder(), filename);
    }

    /**
     * Download the repository as zip file from GitHub and save it to {@link #cacheFile()}.
     *
     * @throws IOException if any io error occurs while downloading the repo
     */
    @SuppressWarnings("PMD.AssignmentInOperand")
    private void download() throws IOException {
        final URL url = new URL(GITHUB_DOWNLOAD_URL
                .replace("{owner}", owner)
                .replace("{repo}", repo)
                .replace("{sha}", sha)
        );
        try (BufferedInputStream input = new BufferedInputStream(url.openStream());
             OutputStream output = Files.newOutputStream(cacheFile().toPath(), CREATE_NEW)) {
            final byte[] dataBuffer = new byte[1024];
            int read;
            while ((read = input.read(dataBuffer, 0, 1024)) != -1) {
                output.write(dataBuffer, 0, read);
            }
        }
    }

    private void extract() {
        //TODO implement
        throw new UnsupportedOperationException("todo implement");
    }
}
