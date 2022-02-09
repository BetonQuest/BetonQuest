package org.betonquest.betonquest.modules.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import lombok.CustomLog;

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
    private static final String GITHUB_BRANCHES_URL = "https://api.github.com/repos/{namespace}/branches/{branch}";


    /**
     * Base url where the files can be downloaded.
     * Owner, repo and commit sha must be replaced with actual values
     */
    private static final String GITHUB_DOWNLOAD_URL = "https://github.com/{namespace}/archive/{sha}.zip";

    /**
     * Used to identify zip entries that are package.yml files
     */
    private static final String PACKAGE_YML = "/package.yml";

    /**
     * The response code 403.
     */
    public static final int RESPONSE_403 = 403;

    /**
     * The BetonQuest Data folder that contains all plugin configuration
     */
    private final File dataFolder;

    /**
     * Namespace of the GitHub repository from which the files are to be downloaded.
     * Format is either {@code user/repo} or {@code organisation/repo}.
     */
    private final String namespace;

    /**
     * Git Tag or Git Branch from which the files should be downloaded
     */
    private final String ref;

    /**
     * A relative path in the remote repository specified by {@code owner} and {@code repo} fields.
     * Only files in this path should be extracted, all other files should remain cached.
     */
    private final String sourcePath;

    /**
     * Path relative to the BetonQuest folder where the files should be placed
     */
    private final String targetPath;

    /**
     * If subpackages should be included recursively.
     */
    private final boolean recurse;

    /**
     * SHA Hash of the commit to which the ref points.
     * Is null before {@link #requestCommitSHA()} has been called.
     */
    private String sha;

    /**
     * Construct a new downloader instance for the given repository and branch.
     * Call {@link #call()} to actually start the download
     *
     * @param dataFolder BetonQuest plugin data folder
     * @param namespace  Github namespace of the repo in the format {@code user/repo} or {@code organisation/repo}.
     * @param ref        Git Tag or Git Branch
     * @param sourcePath what folders to download from the repo
     * @param targetPath where to put the downloaded files
     * @param recurse    if true subpackages will be included recursive, if false don't
     */
    public Downloader(final File dataFolder, final String namespace, final String ref, final String sourcePath, final String targetPath, final boolean recurse) {
        this.dataFolder = dataFolder;
        this.namespace = namespace;
        this.ref = ref;
        this.sourcePath = sourcePath;
        this.targetPath = targetPath;
        this.recurse = recurse;
    }

    /**
     * Strips leading slashes from a path as they should be ignored in any further code.
     * Therefore {@code /quest/lumberjack} is identical to {@code quest/lumberjack}
     *
     * @param path input path where leading slash should be stripped
     * @return path without leading slash
     */
    private String stripLeadingSlash(String path) {
        if (path.startsWith("/")) {
            return path.substring(1);
        } else {
            return path;
        }
    }


    //TODO should result give more details than just returning true?

    //TODO Refined exception handling

    //TODO cleanup old cache files

    //TODO implementation of "offsetPath" as defined in issue #1728

    //TODO support all refs, not only branches

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
                .replace("{namespace}", namespace)
                .replace("{branch}", ref));
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
        final String filename = CACHE_DIR + namespace + "-" + sha.substring(0, 7) + ".zip";
        return new File(dataFolder, filename);
    }

    /**
     * Download the repository as zip file from GitHub and save it to {@link #cacheFile()}.
     *
     * @throws IOException if any io error occurs while downloading the repo
     */
    @SuppressWarnings("PMD.AssignmentInOperand")
    private void download() throws IOException {
        Files.createDirectories(new File(dataFolder, CACHE_DIR).toPath());
        final URL url = new URL(GITHUB_DOWNLOAD_URL
                .replace("{namespace}", namespace)
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
