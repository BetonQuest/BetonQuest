package org.betonquest.betonquest.utils;

import lombok.CustomLog;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * This {@link Updater} checks for new versions on the BeonQuest page and on the GitHub page and download them if wanted.
 */
@CustomLog
public class Updater {
    /**
     * The RELEASE API URL.
     */
    public static final String RELEASE_API_URL = "https://api.github.com/repos/BetonQuest/BetonQuest/releases";
    /**
     * The DEV API URL.
     */
    public static final String DEV_API_URL = "https://betonquest.org/api/v1/";
    /**
     * The API URL path to the latest versions.
     */
    public static final String DEV_API_LATEST = DEV_API_URL + "builds/latest";
    /**
     * The API URL path to the real file for download.
     */
    public static final String DEV_API_DOWNLOAD = DEV_API_URL + "/builds/download/:version/:versionNumber/BetonQuest.jar";
    /**
     * The minimum delay when checking for updates, this prevent to api requests when reloading the plugin too often.
     */
    private static final long CHECK_DELAY = 1000 * 60 * 10;
    /**
     * The minimum delay when we send a player a notification about a new update.
     */
    private static final long NOTIFICATION_DELAY = 1000 * 60 * 60 * 20;
    /**
     * The file name of the plugin in the plugins folder.
     */
    private final String fileName;
    /**
     * The last timestamp, when a player was notified.
     */
    private final Map<UUID, Long> lastNotification = new HashMap<>();
    /**
     * The latest version, the key is a {@link Version} and the value is the URL for the download.
     * If the URL is empty, the version is the current installed one or the already downloaded one.
     */
    private Pair<Version, String> latest;
    /**
     * The last timestamp, when a update was searched.
     */
    private long lastCheck;
    /**
     * The update notification
     */
    private String updateNotification;

    /**
     * Create a new Updater instance.
     *
     * @param currentVersion The current plugin version.
     * @param file           The file of the plugin in the plugins folder.
     */
    public Updater(final String currentVersion, final File file) {
        this.fileName = file.getName();
        this.latest = Pair.of(new Version(currentVersion), null);
        searchUpdate();
    }

    private String getUpdateNotification(final UpdaterConfig config) {
        final String version = "Found newer version '" + latest.getKey().getVersion() + "', ";
        final String automatic = " automatically installed on the next restart!";
        final String automaticProgress = "it will be downloaded and" + automatic;
        final String automaticDone = "it was downloaded and will be " + automatic;
        final String command = "it will be installed, if you execute '/q update'!";

        updateNotification = isUpdateAvailable() && config.ingameNotification ? version + (config.automatic ? automaticDone : command) : null;
        return version + (config.automatic ? automaticProgress : command);
    }

    /**
     * Starts an asynchronous search for updates.
     */
    public final void searchUpdate() {
        final UpdaterConfig config = new UpdaterConfig();
        if (!config.enabled) {
            return;
        }
        final long currentTime = new Date().getTime();
        if (lastCheck + CHECK_DELAY > currentTime) {
            return;
        }
        lastCheck = currentTime;

        Bukkit.getScheduler().runTaskAsynchronously(BetonQuest.getInstance(), () -> {
            searchUpdateTask(config);
            if (latest.getValue() != null) {

                LOG.info(null, getUpdateNotification(config));
                if (config.automatic) {
                    update(Bukkit.getConsoleSender());
                }
            }
        });
    }

    private void searchUpdateTask(final UpdaterConfig config) {
        try {
            searchUpdateTaskRelease(config);
        } catch (final UnknownHostException e) {
            LOG.warning(null, "The update url for releases builds is not reachable!");
        } catch (final IOException e) {
            LOG.warning(null, "Could not get the latest release!", e);
        }
        if (!(isUpdateAvailable() && config.forcedStrategy) && config.updateStrategy.isDev) {
            try {
                searchUpdateTaskDev(config);
            } catch (final UnknownHostException e) {
                LOG.warning(null, "The update url for dev builds is not reachable!");
            } catch (final IOException e) {
                LOG.warning(null, "Could not get the latest dev build number!", e);
            }
        }
    }

    private void searchUpdateTaskRelease(final UpdaterConfig config) throws IOException {
        final JSONArray releaseArray = new JSONArray(readStringFromURL(RELEASE_API_URL));
        for (int index = 0; index < releaseArray.length(); index++) {
            final JSONObject release = releaseArray.getJSONObject(index);
            final Version version = new Version(release.getString("tag_name").substring(1));
            final JSONArray assetsArray = release.getJSONArray("assets");
            for (int i = 0; i < assetsArray.length(); i++) {
                final JSONObject asset = assetsArray.getJSONObject(i);
                if ("BetonQuest.jar".equals(asset.getString("name"))) {
                    final String url = asset.getString("browser_download_url");
                    if (latest.getKey().isNewer(version, config.updateStrategy)) {
                        latest = Pair.of(version, url);
                    }
                }
            }
        }
    }

    private void searchUpdateTaskDev(final UpdaterConfig config) throws IOException {
        final JSONObject json = new JSONObject(readStringFromURL(DEV_API_LATEST));
        final Iterator<String> keys = json.keys();
        while (keys.hasNext()) {
            final String key = keys.next();
            final String dev = json.getString(key);
            final Version version = new Version(key + "-DEV-" + dev);
            final String url = DEV_API_DOWNLOAD.replace(":versionNumber", dev).replace(":version", key);
            if (latest.getKey().isNewer(version, config.updateStrategy)) {
                latest = Pair.of(version, url);
            }
        }
    }

    /**
     * Return if a new version is available.
     *
     * @return True, if a update is available.
     */
    public boolean isUpdateAvailable() {
        return latest.getValue() != null;
    }

    /**
     * Return the new version string.
     *
     * @return The version string or null if there is no newer version.
     */
    public String getUpdateVersion() {
        if (latest.getValue() != null) {
            return latest.getKey().getVersion();
        }
        return null;
    }

    /**
     * Send a update notification to a player.
     *
     * @param player The player, that should receive the message.
     */
    public void sendUpdateNotification(final Player player) {
        if (updateNotification != null && isUpdateAvailable()) {
            final long currentTime = new Date().getTime();
            if (lastNotification.getOrDefault(player.getUniqueId(), 0L) + NOTIFICATION_DELAY > currentTime) {
                return;
            }
            lastNotification.put(player.getUniqueId(), currentTime);
            player.sendMessage(BetonQuest.getInstance().getPluginTag() + ChatColor.DARK_GREEN + updateNotification);
        }
    }

    /**
     * Download the newest version of the plugin and store it in the /plugins/update/ folder.
     *
     * @param sender The {@link CommandSender} that should receive the update related messages.
     */
    public void update(final CommandSender sender) {
        Bukkit.getScheduler().runTaskAsynchronously(BetonQuest.getInstance(), () -> {
            try {
                final UpdaterConfig config = new UpdaterConfig();
                if (!config.enabled) {
                    throw new QuestRuntimeException("The updater is disabled! Change config entry 'update.enabled' to 'true' to enable it.");
                }
                final Version version = latest.getKey();
                searchUpdateTask(config);
                if (version != latest.getKey()) {
                    getUpdateNotification(config);
                    throw new QuestRuntimeException("Update aborted! A newer version was found. New version '"
                            + getUpdateVersion() + "'! You can execute '/q update' again to update.");
                }

                if (latest.getValue() == null) {
                    throw new QuestRuntimeException("The updater did not find an update!"
                            + " This can depend on your update_strategy, check config entry 'update.update_strategy'.");
                }
                sendMessage(sender, ChatColor.DARK_GREEN + "Started update to version '" + latest.getKey().getVersion() + "'...");
                final File folder = Bukkit.getUpdateFolderFile();
                if (!folder.exists() && !folder.mkdirs()) {
                    throw new QuestRuntimeException("The updater could not create the folder '" + folder.getName() + "'!");
                }

                updateDownloadToFile(folder);
                sendMessage(sender, ChatColor.DARK_GREEN + "...download finished. Restart the server to update the plugin.");
            } catch (final QuestRuntimeException e) {
                sendMessage(sender, ChatColor.RED + e.getMessage());
                LOG.debug(null, "Error while performing update!", e);
            }
        });
    }

    private void sendMessage(final CommandSender sender, final String message) {
        LOG.info(null, message);
        if (sender != null && !(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage(BetonQuest.getInstance().getPluginTag() + message);
        }
    }

    @SuppressWarnings("PMD.CyclomaticComplexity")
    private void updateDownloadToFile(final File folder) throws QuestRuntimeException {
        final File file = new File(folder, fileName + ".tmp");
        file.deleteOnExit();
        try {
            if (file.exists()) {
                throw new QuestRuntimeException("The file '" + file.getName() + "' already exists!" +
                        " Please wait for the currently running update to finish. If no update is running delete the file manually.");
            }
            if (!file.createNewFile()) {
                throw new QuestRuntimeException("The updater could not create the file '" + file.getName() + "'!");
            }
            updateDownloadToFileFromURL(file);
            if (!file.renameTo(new File(folder, fileName))) {
                throw new QuestRuntimeException("Could not rename the downloaded file."
                        + " Try running '/q update' again. If it still does not work use a manual download.");
            }
            latest = Pair.of(latest.getKey(), null);
        } catch (final IOException e) {
            if (file.exists()) {
                final boolean deleted = file.delete();
                if (!deleted) {
                    throw new QuestRuntimeException("Download was interrupted! A broken file is in '/plugins/update'."
                            + " Delete this file or the updater will not work anymore. Afterwards you can try running"
                            + " '/q update' again. If it still does not work use a manual download.", e);
                }
            }
            throw new QuestRuntimeException("Could not download the file. Try running '/q update' again."
                    + " If it still does not work use a manual download.", e);
        }
    }

    private void updateDownloadToFileFromURL(final File file) throws IOException {
        FileUtils.copyURLToFile(new URL(latest.getValue()), file, 5000, 5000);
    }

    private String readStringFromURL(final String url) throws IOException {
        try (InputStream inputStream = new URL(url).openStream()) {
            return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        }
    }

    /**
     * Represent different strategies to select witch versions are valid to update to them.
     */
    public enum UpdateStrategy {
        MAJOR(false),
        MINOR(false),
        PATCH(false),
        MAJOR_DEV(true),
        MINOR_DEV(true),
        PATCH_DEV(true);

        /**
         * Trie, if the {@link UpdateStrategy} is a DEV Strategy.
         */
        public final boolean isDev;

        UpdateStrategy(final boolean isDev) {
            this.isDev = isDev;
        }

        /**
         * Convert the current {@link UpdateStrategy} to the equivalent DEV {@link UpdateStrategy}.
         *
         * @return The DEV {@link UpdateStrategy}.
         */
        public UpdateStrategy toDev() {
            switch (this) {
                case MAJOR:
                    return MAJOR_DEV;
                case MINOR:
                    return MINOR_DEV;
                case PATCH:
                    return PATCH_DEV;
                default:
                    return this;
            }
        }
    }

    /**
     * This is an abstract representation of a version.
     */
    public static class Version {
        /**
         * This is the BetonQuest related TAG for DEV-Builds in versions.
         */
        public static final String DEV_TAG = "DEV-";

        /**
         * The raw version string.
         */
        private final String versionString;
        /**
         * This is a help object that split the versionString into MAJOR, MINOR and PATCH.
         */
        private final DefaultArtifactVersion artifactVersion;
        /**
         * If the version is in a official DEV format, this is true.
         */
        private final Integer dev;
        /**
         * If the version is not a RELEASE or a DEV version this is true.
         */
        private final boolean unofficial;

        /**
         * Create a new Version.
         *
         * @param versionString The raw version string.
         */
        public Version(final String versionString) {
            this.versionString = versionString;
            this.artifactVersion = new DefaultArtifactVersion(versionString);

            Integer dev = null;
            boolean unofficial = false;
            final String qualifier = artifactVersion.getQualifier();
            try {
                if (qualifier != null) {
                    dev = Integer.valueOf(qualifier.substring(DEV_TAG.length()));
                }
            } catch (final NumberFormatException e) {
                unofficial = true;
            }
            this.dev = dev;
            this.unofficial = unofficial;
        }

        /**
         * Create a new Version.
         *
         * @param versionString The Version that should be copied.
         */
        public Version(final Version versionString) {
            this.versionString = versionString.versionString;
            this.artifactVersion = versionString.artifactVersion;
            this.dev = versionString.dev;
            this.unofficial = versionString.unofficial;
        }

        /**
         * Check if a other Version is newer than this Version.
         *
         * @param version        The {@link Version} to check for.
         * @param updateStrategy The {@link UpdateStrategy} for checking.
         * @return True if version is newer then this version.
         */
        public boolean isNewer(final Version version, final UpdateStrategy updateStrategy) {
            if (version.isUnofficial() || !updateStrategy.isDev && version.isDev()) {
                return false;
            }
            final int majorVersion = Integer.compare(artifactVersion.getMajorVersion(), version.artifactVersion.getMajorVersion());
            final int minorVersion = Integer.compare(artifactVersion.getMinorVersion(), version.artifactVersion.getMinorVersion());
            final int patchVersion = Integer.compare(artifactVersion.getIncrementalVersion(), version.artifactVersion.getIncrementalVersion());
            return isNewerCheckStrategy(version, updateStrategy, majorVersion, minorVersion, patchVersion);
        }

        @SuppressWarnings("PMD.CyclomaticComplexity")
        private boolean isNewerCheckStrategy(final Version version, final UpdateStrategy updateStrategy, final int majorVersion, final int minorVersion, final int patchVersion) {
            switch (updateStrategy) {
                case MAJOR:
                case MAJOR_DEV:
                    if (majorVersion > 0) {
                        return false;
                    } else if (majorVersion < 0) {
                        return true;
                    }
                case MINOR:
                case MINOR_DEV:
                    if (majorVersion == 0) {
                        if (minorVersion > 0) {
                            return false;
                        } else if (minorVersion < 0) {
                            return true;
                        }
                    }
                case PATCH:
                case PATCH_DEV:
                    if (majorVersion == 0 && minorVersion == 0) {
                        return isNewerPatch(version, patchVersion);
                    }
                default:
                    return false;
            }
        }

        private boolean isNewerPatch(final Version version, final int patchVersion) {
            if (patchVersion > 0) {
                return false;
            } else if (patchVersion < 0) {
                return true;
            } else {
                final Integer thisDev = isDev() ? dev : isUnofficial() ? 0 : null;
                final Integer targetDev = version.isDev() ? version.dev : version.isUnofficial() ? 0 : null;
                if (thisDev == null || targetDev == null) {
                    return thisDev != null;
                } else {
                    return thisDev.compareTo(targetDev) < 0;
                }
            }
        }

        /**
         * Get the version string.
         *
         * @return The string of this version.
         */
        public String getVersion() {
            return versionString;
        }

        /**
         * Returns whether this is an DEV version or not.
         *
         * @return True, if this version is an DEV version.
         */
        public boolean isDev() {
            return dev != null;
        }

        /**
         * Returns whether this version is official or not.
         *
         * @return True, if this version is not an official version.
         */
        public boolean isUnofficial() {
            return unofficial;
        }
    }

    /**
     * Represent the Updater related configuration.
     */
    private class UpdaterConfig {
        /**
         * True, if the updater is enabled.
         */
        private final boolean enabled;
        /**
         * The selected {@link UpdateStrategy}.
         */
        private final UpdateStrategy updateStrategy;
        /**
         * True, if updated should be downloaded automatic.
         */
        private final boolean automatic;
        /**
         * Should player be notified ingame.
         */
        private final boolean ingameNotification;
        /**
         * True, if the {@link UpdateStrategy} was forced to a DEV UpdateStrategy.
         */
        private final boolean forcedStrategy;

        /**
         * Reads the configuration file.
         */
        public UpdaterConfig() {
            final FileConfiguration config = BetonQuest.getInstance().getConfig();

            enabled = config.getBoolean("update.enabled", true);
            ingameNotification = config.getBoolean("update.ingameNotification", true);

            final String stringUpdateStrategy = config.getString("update.strategy");
            UpdateStrategy strategy;
            try {
                strategy = stringUpdateStrategy == null ? UpdateStrategy.MINOR :
                        UpdateStrategy.valueOf(stringUpdateStrategy.toUpperCase(Locale.ROOT));
            } catch (final IllegalArgumentException exception) {
                LOG.error(null, "Could not read 'update.strategy' in 'config.yml'!", exception);
                strategy = UpdateStrategy.MINOR;
            }

            if (latest.getKey().isDev() && !strategy.isDev || latest.getKey().isUnofficial()) {
                updateStrategy = strategy.toDev();
                automatic = false;
                forcedStrategy = true;
            } else {
                updateStrategy = strategy;
                automatic = config.getBoolean("update.automatic", false);
                forcedStrategy = false;
            }
        }
    }
}
