package org.betonquest.betonquest.web.downloader;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.FileVisitResult.CONTINUE;

/**
 * Downloads files from any public GitHub repository and extracts them to your QuestPackages folder.
 */
@SuppressWarnings({"PMD.GodClass", "PMD.TooManyMethods"})
public class Downloader implements Callable<Boolean> {
    /**
     * Values that are allowed as {@link #offsetPath}.
     * Currently, only {@code QuestPackages} and {@code QuestTemplates}
     */
    public static final List<String> ALLOWED_OFFSET_PATHS = List.of("QuestPackages", "QuestTemplates");

    /**
     * The http status code 400 - Bad Request
     */
    public static final int RESPONSE_400 = 400;

    /**
     * Directory where downloaded repositories should be cached
     */
    private static final String CACHE_DIR = ".cache/downloader/";

    /**
     * Base URL of the GitHub refs RestAPI.
     * Namespace and ref must be replaced with actual values.
     */
    private static final String GITHUB_REFS_URL = "https://api.github.com/repos/{namespace}/git/{ref}";

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
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

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
     * If files shall be overwritten, otherwise an exception is thrown
     */
    private final boolean overwrite;

    /**
     * SHA Hash of the commit to which the ref points.
     * Is null before {@link #requestCommitSHA()} has been called.
     */
    @Nullable
    private String sha;

    /**
     * Constructs a new downloader instance for the given repository and branch.
     * Call {@link #call()} to actually start the download
     *
     * @param log        the logger that will be used for logging
     * @param dataFolder BetonQuest plugin data folder
     * @param namespace  GitHub namespace of the repo in the format {@code user/repo} or {@code organisation/repo}.
     * @param ref        Git Tag or Git Branch
     * @param offsetPath {@code QuestPackages} or {@code QuestTemplates}
     * @param sourcePath what folders to download from the repo
     * @param targetPath where to put the downloaded files
     * @param recurse    if true subpackages will be included recursive, if false don't
     * @param overwrite  if true existing files will be overwritten, if false an exception is thrown
     */
    public Downloader(final BetonQuestLogger log, final File dataFolder, final String namespace, final String ref, final String offsetPath,
                      final String sourcePath, final String targetPath, final boolean recurse, final boolean overwrite) {
        this.log = log;
        this.dataFolder = dataFolder.toPath();
        this.namespace = namespace;
        this.ref = ref;
        this.offsetPath = offsetPath;
        this.sourcePath = sourcePath;
        this.targetPath = targetPath;
        this.recurse = recurse;
        this.overwrite = overwrite;
    }

    /**
     * Runs the downloader with the specified settings.
     *
     * @return result of the download, generally true
     * @throws Exception if any exception occurred during download
     */
    @Override
    public Boolean call() throws Exception {
        requestCommitSHA();
        final Path cacheFile = getCacheFile();
        if (Files.exists(cacheFile)) {
            log.debug(cacheFile + " is already cached, reusing it");
        } else {
            download();
            cleanupOld();
        }
        extract();
        return true;
    }

    /**
     * Performs a get request to the GitHub RestAPI to retrieve the SHA hash of the latest commit on the branch.
     *
     * @return the commit sha
     * @throws DownloadFailedException if the download fails due to any qualified error
     * @throws IOException             if any io error occurs during request or parsing
     */
    @SuppressWarnings("PMD.CyclomaticComplexity")
    private void requestCommitSHA() throws DownloadFailedException, IOException {
        if (!ref.startsWith("refs/")) {
            if (!ref.matches("[0-9a-f]{7,40}")) {
                throw new DownloadFailedException("ref is not a valid commit SHA");
            }
            this.sha = ref;
            log.debug("Commit has sha '" + this.sha + "'");
            return;
        }
        final URL url = new URL(GITHUB_REFS_URL
                .replace("{namespace}", namespace)
                .replace("{ref}", ref));
        log.debug("Requesting commit sha for " + namespace + " at " + ref + " from github api");
        final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.connect();
        final int code = connection.getResponseCode();
        if (code >= RESPONSE_400) {
            throw switch (code) {
                case 403 ->
                        new DownloadFailedException("It looks like too many requests were made to the github api, please wait until you have been unblocked.");
                case 404 -> new DownloadFailedException("404 Not Found - are namespace and ref name correct?");
                default -> new DownloadFailedException("github api returned error code " + code);
            };
        }
        try (InputStreamReader reader = new InputStreamReader(connection.getInputStream(), UTF_8)) {
            final JsonElement object = Optional.ofNullable(JsonParser.parseReader(reader).getAsJsonObject().get("object")).orElseThrow();
            final Optional<JsonElement> type = Optional.ofNullable(object.getAsJsonObject().get("type"));
            if (type.stream().map(JsonElement::getAsString).noneMatch("commit"::equals)) {
                throw new DownloadFailedException("ref does not point to a commit");
            }
            final Optional<JsonElement> sha = Optional.ofNullable(object.getAsJsonObject().get("sha"));
            this.sha = sha.orElseThrow().getAsString();
            log.debug("Commit has sha '" + this.sha + "'");
        } catch (JsonParseException | NoSuchElementException | IllegalStateException e) {
            throw new IOException("Unable to parse the JSON returned by Github API", e);
        }
    }

    /**
     * Gets the full source path including the offsetPath
     *
     * @return full source path
     */
    private String getFullSourcePath() {
        return offsetPath + (sourcePath.startsWith("/") ? "" : "/") + sourcePath + (sourcePath.endsWith("/") ? "" : "/");
    }

    /**
     * Gets the full target path including the offsetPath
     *
     * @return full target path
     */
    private String getFullTargetPath() {
        return offsetPath + (targetPath.startsWith("/") ? "" : "/") + targetPath + (targetPath.endsWith("/") ? "" : "/");
    }

    /**
     * A short form of the {@link #ref} for use in filenames.
     * All {@code /} chars will be replaced with {@code _}
     *
     * @return shortened form of the ref
     */
    private String getShortRef() {
        final String shortRef;
        if (ref.startsWith("refs/heads/")) {
            shortRef = "b" + ref.substring(11);
        } else if (ref.startsWith("refs/tags/")) {
            shortRef = "t" + ref.substring(10);
        } else {
            shortRef = "_" + ref;
        }
        return shortRef.replace('/', '_');
    }

    /**
     * Gets the file inside the cache directory where the repo is cached.
     *
     * @return zip file containing the repo data
     */
    private Path getCacheFile() throws IOException {
        if (sha == null) {
            throw new IOException("Can't get a file without SHA!");
        }
        final String filename = namespace + "-" + getShortRef() + "-" + sha.substring(0, 7) + ".zip";
        return dataFolder.resolve(CACHE_DIR).resolve(filename);
    }

    /**
     * Checks if the supplied file is a cache file for this ref.
     *
     * @param file any file to check
     * @return true if a cached file (maybe an old one), false otherwise
     */
    private boolean isCacheFile(final Path file) throws IOException {
        if (file.toAbsolutePath().startsWith(getCacheFile().toAbsolutePath().getParent())) {
            final String fileIdentifier = namespace.substring(namespace.lastIndexOf('/') + 1) + "-" + getShortRef();
            return Optional.ofNullable(file.getFileName()).map(Path::toString).stream()
                    .anyMatch(s -> s.startsWith(fileIdentifier));
        } else {
            return false;
        }
    }

    /**
     * Downloads the repository as a zip file from GitHub and saves it to {@link #getCacheFile()}.
     *
     * @throws IOException if any io error occurs while downloading the repo
     */
    @SuppressWarnings("PMD.AssignmentInOperand")
    private void download() throws IOException, DownloadFailedException {
        Files.createDirectories(Optional.ofNullable(getCacheFile().getParent()).orElseThrow());
        if (sha == null) {
            throw new DownloadFailedException("There is no commit SHA!");
        }
        final URL url = new URL(GITHUB_DOWNLOAD_URL
                .replace("{namespace}", namespace)
                .replace("{sha}", sha)
        );
        try (BufferedInputStream input = new BufferedInputStream(url.openStream());
             OutputStream output = Files.newOutputStream(getCacheFile(), StandardOpenOption.WRITE, StandardOpenOption.CREATE_NEW)) {
            final byte[] dataBuffer = new byte[1024];
            int read;
            while ((read = input.read(dataBuffer, 0, 1024)) != -1) {
                output.write(dataBuffer, 0, read);
            }
            log.debug("Repo has been saved to cache as " + getCacheFile());
        } catch (final FileNotFoundException e) {
            throw new DownloadFailedException("The commit SHA '" + this.sha + "' does not exist!", e);
        }
    }

    /**
     * Extracts the files placed at the {@link #sourcePath} from the cached zip and places them in the {@link #targetPath}.
     *
     * @throws DownloadFailedException if the download fails due to any qualified error
     * @throws IOException             if any unhandled io error occurs while extracting the zip
     */
    @SuppressWarnings("PMD.AssignmentInOperand")
    private void extract() throws DownloadFailedException, IOException {
        log.debug("Extracting downloaded files from cache");
        final Set<String> packages = listAllPackagesInZip();
        final List<String> ignoredPackages = filterIgnoredPackagesInZip(packages);
        ignoredPackages.forEach(packages::remove);
        if (!ignoredPackages.isEmpty()) {
            log.debug("Ignoring the following sub packages:");
            ignoredPackages.stream().map(s -> "    " + s).forEach(log::debug);
        }
        checkAnyPackageOverwritten(packages);
        boolean foundAny = false;
        try (ZipInputStream input = new ZipInputStream(Files.newInputStream(getCacheFile(), StandardOpenOption.READ))) {
            ZipEntry entry;
            while ((entry = input.getNextEntry()) != null) {
                if (isChildOfPath(entry)) {
                    foundAny = true;
                    final String name = stripRootDir(entry.getName());
                    if (recurse || ignoredPackages.stream().noneMatch(name::startsWith)) {
                        extractEntry(input, entry);
                    }
                }
            }
        }
        if (!foundAny) {
            throw new DownloadFailedException("repo contained no files at '" + getFullSourcePath() + "'");
        }
    }

    /**
     * Extracts a single entry from the provided zip input and saves it to the {@code sourcePath}.
     *
     * @param input zip input stream used to read the zip file
     * @param entry entry to extract from the zip file
     * @throws DownloadFailedException if the download fails due to any qualified error
     * @throws IOException             if any unhandled io exception occurs while extraction
     */
    private void extractEntry(final ZipInputStream input, final ZipEntry entry) throws DownloadFailedException, IOException {
        final String relative = stripRootDir(entry.getName()).replace(getFullSourcePath(), "");
        final Path newFile = dataFolder.resolve(getFullTargetPath() + relative).normalize();
        checkSecurityRestrictions(newFile);
        if (entry.isDirectory()) {
            Files.createDirectories(newFile);
        } else {
            Files.createDirectories(Optional.ofNullable(newFile.getParent()).orElseThrow());
            final StandardOpenOption[] options;
            if (overwrite) {
                options = new StandardOpenOption[]{StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING};
            } else {
                options = new StandardOpenOption[]{StandardOpenOption.WRITE, StandardOpenOption.CREATE_NEW};
            }
            try (OutputStream out = Files.newOutputStream(newFile, options)) {
                input.transferTo(out);
                log.debug("Extracted " + newFile);
            } catch (final FileAlreadyExistsException e) {
                throw new DownloadFailedException("File already exists: " + e.getMessage(), e);
            }
        }
    }

    /**
     * Checks if a file at the given Path may be created by the downloader or if some security restrictions deny it.
     * <p>
     * Specifically the BetonQuest folder should act as a sandbox and the Downloader should not be allowed to download
     * anything to a place outside this folder.
     *
     * @param newFile path of the file that the downloader tries to create
     * @throws SecurityException if the file is outside the BetonQuest folder or would overwrite the BetonQuest config
     */
    private void checkSecurityRestrictions(final Path newFile) {
        if (!newFile.startsWith(dataFolder.normalize())) {
            throw new SecurityException("'" + newFile + "' is not a child of BetonQuest data folder");
        }
        if (ALLOWED_OFFSET_PATHS.stream().noneMatch(path -> newFile.startsWith(dataFolder.resolve(path).normalize()))) {
            throw new SecurityException("'" + newFile + "' is not a valid target");
        }
        if (newFile.equals(dataFolder.resolve("config.yml").normalize())) {
            throw new SecurityException("Download tried to overwrite BetonQuest config. Aborting for security reasons!");
        }
    }

    /**
     * Cleanups any old cache files for the same ref.
     *
     * @throws IOException any io error while cleanup
     */
    private void cleanupOld() throws IOException {
        final Path cacheDir = getCacheFile().getParent();
        log.debug("Cleaning up any old files from cache");
        Files.walkFileTree(cacheDir, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
                if (attrs.isRegularFile() && isCacheFile(file) && !file.equals(getCacheFile())) {
                    Files.delete(file);
                    log.debug("Deleted " + file);
                    return CONTINUE;
                } else {
                    return super.visitFile(file, attrs);
                }
            }
        });
    }

    /**
     * Returns only subpackages that shall not be extracted from the zip.
     * If {@link #recurse} flag is set to true an empty list will be returned as even subpackages shall be included.
     *
     * @param packagesAll collection that contains all packages in the zip
     * @return a list of all sub packages or an empty list
     */
    private List<String> filterIgnoredPackagesInZip(final Collection<String> packagesAll) {
        if (recurse) {
            return List.of();
        } else {
            return packagesAll.stream().filter(pack ->
                    packagesAll.stream()
                            .filter(other -> !other.equals(pack))
                            .anyMatch(pack::startsWith)
            ).toList();
        }
    }

    /**
     * Lists all packages in the cached zip file, including subpackages.
     *
     * @return a mutable set containing all package directories
     * @throws IOException for any occurring io error
     */
    @SuppressWarnings("PMD.AssignmentInOperand")
    private Set<String> listAllPackagesInZip() throws IOException {
        final Set<String> packagesAll = new HashSet<>();
        try (ZipInputStream input = new ZipInputStream(Files.newInputStream(getCacheFile(), StandardOpenOption.READ))) {
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
        return name.substring(0, name.length() - PACKAGE_YML.length());
    }

    /**
     * Check if overwrite flag is not set and any package would be overwritten by extracting.
     * If this is the case an exception is thrown.
     *
     * @param packages packages created when extracting the zip
     * @throws DownloadFailedException exception thrown if package would get overwritten
     */
    private void checkAnyPackageOverwritten(final Set<String> packages) throws DownloadFailedException {
        if (overwrite) {
            return;
        }
        final Optional<Path> existing = packages.stream()
                .map(packageName -> packageName.replace(getFullSourcePath(), ""))
                .map(packageName -> dataFolder.resolve(getFullTargetPath()).resolve(packageName).resolve(PACKAGE_YML))
                .filter(Files::exists)
                .findAny();
        if (existing.isPresent()) {
            throw new DownloadFailedException("package already exists: "
                    + dataFolder.resolve(offsetPath).relativize(existing.get()).getParent());
        }
    }
}
