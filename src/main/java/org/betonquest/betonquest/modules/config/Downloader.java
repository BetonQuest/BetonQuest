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
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.StandardOpenOption.CREATE_NEW;
import static java.nio.file.StandardOpenOption.READ;

/**
 * Download files from any public GitHub repository and extract them to your QuestPackages folder.
 */
@CustomLog(topic = "Downloader")
public class Downloader implements Callable<Boolean> {

    /**
     * Directory where downloaded repositories should be cached
     */
    private static final String CACHE_DIR = ".cache/downloader/";

    /**
     * Base URL of the GitHub refs RestAPI.
     * Namespace and ref must be replaced with actual values.
     */
    private static final String GITHUB_REFS_URL = "https://api.github.com/repos/{namespace}/git/ref/{ref}";


    /**
     * Base url where the files can be downloaded.
     * Namespace and commit sha must be replaced with actual values
     */
    private static final String GITHUB_DOWNLOAD_URL = "https://github.com/{namespace}/archive/{sha}.zip";

    /**
     * Used to identify zip entries that are package.yml files
     */
    private static final String PACKAGE_YML = "package.yml";

    /**
     * The response code 403.
     */
    public static final int RESPONSE_403 = 403;

    /**
     * The BetonQuest Data folder that contains all plugin configuration
     */
    private final Path dataFolder;

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
     * Which folder from the repository format to select as root folder:
     * {@code QuestPackages} or {@code QuestTemplates}.
     */
    private final String offsetPath;

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
     * @param offsetPath {@code QuestPackages} or {@code QuestTemplates}
     * @param sourcePath what folders to download from the repo
     * @param targetPath where to put the downloaded files
     * @param recurse    if true subpackages will be included recursive, if false don't
     */
    public Downloader(final File dataFolder, final String namespace, final String ref, final String offsetPath, final String sourcePath, final String targetPath, final boolean recurse) {
        this.dataFolder = dataFolder.toPath();
        this.namespace = namespace;
        this.ref = ref;
        this.offsetPath = offsetPath;
        this.sourcePath = sourcePath;
        this.targetPath = targetPath;
        this.recurse = recurse;
    }

    //TODO should result give more details than just returning true?

    //TODO Refined exception handling

    /**
     * Run the downloader with the specified settings
     *
     * @return result of the download, generally true
     * @throws Exception if any exception occurred during download
     */
    @Override
    public Boolean call() throws Exception {
        requestCommitSHA();
        if (!Files.exists(getCacheFile())) {
            download();
            cleanupOld();
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
        final URL url = new URL(GITHUB_REFS_URL
                .replace("{namespace}", namespace)
                .replace("{ref}", ref));
        final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.connect();
        final int code = connection.getResponseCode();
        if (code == RESPONSE_403) {
            throw new IOException("It looks like too many requests were made to the github api, please wait until you have been unblocked.");
        }
        try (InputStreamReader reader = new InputStreamReader(connection.getInputStream(), UTF_8)) {
            final JsonElement object = Optional.ofNullable(JsonParser.parseReader(reader).getAsJsonObject().get("object")).orElseThrow();
            final Optional<JsonElement> type = Optional.ofNullable(object.getAsJsonObject().get("type"));
            if (type.stream().map(JsonElement::getAsString).noneMatch("commit"::equals)) {
                throw new IOException("ref does not point to a commit");
            }
            final Optional<JsonElement> sha = Optional.ofNullable(object.getAsJsonObject().get("sha"));
            this.sha = sha.orElseThrow().getAsString();
        } catch (JsonParseException | NoSuchElementException | IllegalStateException e) {
            throw new IOException("Unable to parse the JSON returned by Github API", e);
        }
    }

    /**
     * Get the full source path including the offsetPath
     *
     * @return full source path
     */
    private String getFullSourcePath() {
        return offsetPath + "/" + sourcePath;
    }

    /**
     * Get the full target path including the offsetPath
     *
     * @return full target path
     */
    private String getFullTargetPath() {
        return offsetPath + "/" + targetPath;
    }

    /**
     * A short form of the {@link #ref} for use in filenames.
     * All {@code /} chars will be replaced with {@code _}
     *
     * @return shortened form of the ref
     */
    private String getShortRef() {
        String shortRef;
        if (ref.startsWith("heads/")) {
            shortRef = "b" + ref.substring(6);
        } else if (ref.startsWith("tags/")) {
            shortRef = "t" + ref.substring(5);
        } else {
            shortRef = "_" + ref;
        }
        return shortRef.replace('/', '_');
    }

    /**
     * The file inside the cache directory where the repo is cached
     *
     * @return zip file containing the repo data
     */
    private Path getCacheFile() {
        final String filename = CACHE_DIR + namespace + "-" + getShortRef() + "-" + sha.substring(0, 7) + ".zip";
        return dataFolder.resolve(filename);
    }

    /**
     * Checks if the supplied file is a cache file for this ref
     *
     * @param file any file to check
     * @return true if a cached file (maybe an old one), false otherwise
     */
    private boolean isCacheFile(final Path file) {
        if (file.toAbsolutePath().startsWith(getCacheFile().toAbsolutePath().getParent())) {
            final String fileIdentifier = namespace.substring(namespace.lastIndexOf('/') + 1) + "-" + getShortRef();
            return Optional.ofNullable(file.getFileName()).map(Path::toString).stream()
                    .anyMatch(s -> s.startsWith(fileIdentifier));
        } else {
            return false;
        }
    }

    /**
     * Download the repository as zip file from GitHub and save it to {@link #getCacheFile()}.
     *
     * @throws IOException if any io error occurs while downloading the repo
     */
    @SuppressWarnings("PMD.AssignmentInOperand")
    private void download() throws IOException {
        Files.createDirectories(dataFolder.resolve(CACHE_DIR));
        final URL url = new URL(GITHUB_DOWNLOAD_URL
                .replace("{namespace}", namespace)
                .replace("{sha}", sha)
        );
        try (BufferedInputStream input = new BufferedInputStream(url.openStream());
             OutputStream output = Files.newOutputStream(getCacheFile(), CREATE_NEW)) {
            final byte[] dataBuffer = new byte[1024];
            int read;
            while ((read = input.read(dataBuffer, 0, 1024)) != -1) {
                output.write(dataBuffer, 0, read);
            }
        }
    }

    /**
     * Extract the files placed at {@link #sourcePath} from the cached zip and place them in {@link #targetPath}
     *
     * @throws IOException if any io error occurs while extracting the zip
     */
    @SuppressWarnings("PMD.AssignmentInOperand")
    private void extract() throws IOException {
        final List<String> subPackages = listIgnoredPackagesInZip();
        boolean foundAny = false;
        try (ZipInputStream input = new ZipInputStream(Files.newInputStream(getCacheFile(), READ))) {
            ZipEntry entry;
            while ((entry = input.getNextEntry()) != null) {
                if (isChildOfPath(entry)) {
                    foundAny = true;
                    final String name = stripRootDir(entry.getName());
                    if (recurse || subPackages.stream().noneMatch(name::startsWith)) {
                        extractEntry(input, entry);
                    }
                }
            }
        }
        if (!foundAny) {
            throw new IOException("repo contained no files at '" + getFullSourcePath() + "'");
        }
    }

    /**
     * Extract a single entry from the provided zip input and save it to {@code sourcePath}.
     *
     * @param input zip input stream used to read the zip file
     * @param entry entry to extract from the zip file
     * @throws IOException if any io exception occurs while extraction
     */
    private void extractEntry(final ZipInputStream input, final ZipEntry entry) throws IOException {
        final String relative = stripRootDir(entry.getName()).replace(getFullSourcePath(), "");
        final Path newFile = dataFolder.resolve(getFullTargetPath() + relative);
        if (!newFile.toRealPath().startsWith(dataFolder.toRealPath())) {
            throw new IOException("'" + newFile + "' is not a child of BetonQuest data folder");
        }
        if (!newFile.toRealPath().startsWith(dataFolder.resolve(offsetPath).toRealPath())) {
            throw new IOException("'" + newFile + "' is not a child of " + offsetPath + " folder");
        }
        if (!newFile.toRealPath().equals(dataFolder.resolve("config.yml").toRealPath())) {
            throw new IOException("Download tried to override BetonQuest config. Aborting for security reasons!");
        }
        Files.createDirectories(Optional.ofNullable(newFile.toAbsolutePath().getParent()).orElseThrow());
        try (OutputStream out = Files.newOutputStream(newFile, CREATE_NEW)) {
            input.transferTo(out);
        }
    }

    /**
     * Cleanup any old cache files for the same ref
     *
     * @throws IOException any io error while cleanup
     */
    private void cleanupOld() throws IOException {
        final Path cacheDir = getCacheFile().getParent();
        Files.walkFileTree(cacheDir, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
                if (attrs.isRegularFile() && isCacheFile(file)) {
                    Files.delete(file);
                    return CONTINUE;
                } else {
                    return super.visitFile(file, attrs);
                }
            }
        });
    }

    /**
     * Lists all subpackages that shall not be extracted from the zip.
     * If {@link #recurse} flag is set to true an empty list will be returned as even subpackages shall be included.
     *
     * @return a list of all sub packages or an empty list
     * @throws IOException for any occurring io error
     */
    private List<String> listIgnoredPackagesInZip() throws IOException {
        if (recurse) {
            return List.of();
        } else {
            final List<String> packagesAll = listAllPackagesInZip();
            return packagesAll.stream().filter(pack ->
                    packagesAll.stream()
                            .filter(other -> !other.equals(pack))
                            .anyMatch(pack::startsWith)
            ).toList();
        }
    }

    /**
     * List all packages in the cached zip file, including subpackages.
     *
     * @return a list containing all package directories
     * @throws IOException for any occurring io error
     */
    @SuppressWarnings("PMD.AssignmentInOperand")
    private List<String> listAllPackagesInZip() throws IOException {
        final List<String> packagesAll = new ArrayList<>();
        try (ZipInputStream input = new ZipInputStream(Files.newInputStream(getCacheFile(), READ))) {
            ZipEntry entry;
            while ((entry = input.getNextEntry()) != null) {
                if (isChildOfPath(entry) && isPackageYML(entry)) {
                    packagesAll.add(getPackageDir(entry));
                }
            }
        }
        return packagesAll;
    }

    /**
     * Zip files downloaded from GitHub always contain a root folder that has the same name as the zip file.
     * This folder shall be stripped from the zip entry name so further code does not need to handle it.
     *
     * @param entryName initial name of the zip entry
     * @return name with the first directory stripped from it
     */
    private String stripRootDir(final String entryName) {
        return entryName.substring(entryName.indexOf('/') + 1);
    }

    /**
     * Checks if the entry is a child of the directory specified by {@link #sourcePath}
     *
     * @param entry the entry to check
     * @return true if a child of sourcePath, false otherwise
     */
    private boolean isChildOfPath(final ZipEntry entry) {
        final String name = stripRootDir(entry.getName());
        return name.startsWith(getFullSourcePath());
    }

    /**
     * Checks if the given zip entry is a package.yml file
     *
     * @param entry entry that should be checked
     * @return true if the entry is a package.yml, false otherwise
     */
    private boolean isPackageYML(final ZipEntry entry) {
        return !entry.isDirectory() && entry.getName().endsWith(PACKAGE_YML);
    }

    /**
     * Returns the directory of the package that is specified by the supplied package yml.
     * Ensure that the ZipEntry passed is a package.yml with {@link #isPackageYML(ZipEntry)} before calling this method.
     *
     * @param packageYml the package.yml as zip entry
     * @return the directory where the entry is located
     */
    private String getPackageDir(final ZipEntry packageYml) {
        final String name = stripRootDir(packageYml.getName());
        return name.substring(0, name.length() - PACKAGE_YML.length() - 1);
    }
}
