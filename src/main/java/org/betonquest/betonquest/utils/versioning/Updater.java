package org.betonquest.betonquest.utils.versioning;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.CustomLog;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;
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
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

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
     * The response code 403.
     */
    public static final int RESPONSE_403 = 403;
    /**
     * The minimum delay when checking for updates, this prevent to api requests when reloading the plugin too often.
     */
    private static final long CHECK_DELAY = 1000 * 60 * 10;
    /**
     * The minimum delay when we send a player a notification about a new update.
     */
    private static final long NOTIFICATION_DELAY = 1000 * 60 * 60 * 20;
    /**
     * The indicator for dev versions.
     */
    private static final String DEV_INDICATOR = "DEV-";
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

                LOG.info(getUpdateNotification(config));
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
            LOG.warning("The update server for release builds is currently not available!");
        } catch (final IOException e) {
            LOG.warning("Could not get the latest release! " + e.getMessage(), e);
        }
        if (!(isUpdateAvailable() && config.forcedStrategy) && config.downloadDev) {
            try {
                searchUpdateTaskDev(config);
            } catch (final UnknownHostException e) {
                LOG.warning("The update server for dev builds is currently not available!");
            } catch (final IOException e) {
                LOG.warning("Could not get the latest dev build! " + e.getMessage(), e);
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
                    final VersionComparator comparator = new VersionComparator(config.strategy);
                    if (comparator.isOtherNewerThanCurrent(latest.getKey(), version)) {
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
            final VersionComparator comparator = new VersionComparator(config.strategy, "DEV-");
            if (comparator.isOtherNewerThanCurrent(latest.getKey(), version)) {
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
                if (!version.equals(latest.getKey())) {
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
                LOG.debug("Error while performing update!", e);
            }
        });
    }

    private void sendMessage(final CommandSender sender, final String message) {
        LOG.info(message);
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

    private String readStringFromURL(final String stringUrl) throws IOException {
        final URL url = new URL(stringUrl);
        final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.connect();
        final int code = connection.getResponseCode();
        if (code == RESPONSE_403) {
            throw new IOException("It looks like too many requests were made to the update server, please wait until you have been unblocked.");
        }
        try (InputStream inputStream = connection.getInputStream()) {
            final String result = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            connection.disconnect();
            return result;
        }
    }

    /**
     * Represent the Updater related configuration.
     */
    private class UpdaterConfig {
        /**
         * Indicator for dev {@link UpdateStrategy}.
         */
        private static final String DEV_INDICATOR = "_DEV";
        /**
         * True, if the updater is enabled.
         */
        private final boolean enabled;
        /**
         * The selected {@link UpdateStrategy}.
         */
        private final UpdateStrategy strategy;
        /**
         * True if dev versions should be downloaded.
         */
        private final boolean downloadDev;
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
        @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
        public UpdaterConfig() {
            final FileConfiguration config = BetonQuest.getInstance().getConfig();

            enabled = config.getBoolean("update.enabled", true);
            ingameNotification = config.getBoolean("update.ingameNotification", true);

            String updateStrategy = config.getString("update.strategy", "MINOR").toUpperCase(Locale.ROOT);
            boolean downloadDev = updateStrategy.endsWith(DEV_INDICATOR);
            if (downloadDev) {
                updateStrategy = updateStrategy.substring(0, updateStrategy.length() - DEV_INDICATOR.length());
            }
            UpdateStrategy strategy;
            try {
                strategy = UpdateStrategy.valueOf(updateStrategy);
            } catch (final IllegalArgumentException exception) {
                LOG.error("Could not parse 'update.strategy' in 'config.yml'!", exception);
                strategy = UpdateStrategy.MINOR;
            }
            this.strategy = strategy;

            final boolean isDev = Updater.DEV_INDICATOR.equals(latest.getKey().getQualifier());
            if (isDev && !downloadDev || !isDev && latest.getKey().hasQualifier()) {
                downloadDev = true;
                automatic = false;
                forcedStrategy = true;
            } else {
                automatic = config.getBoolean("update.automatic", false);
                forcedStrategy = false;
            }
            this.downloadDev = downloadDev;
        }
    }
}
