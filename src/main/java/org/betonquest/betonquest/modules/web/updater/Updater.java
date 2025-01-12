package org.betonquest.betonquest.modules.web.updater;

import org.apache.commons.lang3.tuple.Pair;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.ConfigurationFile;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.modules.versioning.Version;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.Nullable;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.time.InstantSource;
import java.time.temporal.TemporalAmount;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * This {@link Updater} checks for new versions on the BetonQuest page and on the GitHub page and download them if wanted.
 */
public class Updater {
    /**
     * The minimum delay when checking for updates, this prevents too many api requests when reloading the plugin often.
     */
    private static final TemporalAmount CHECK_DELAY = Duration.ofMinutes(10);

    /**
     * The minimum delay when we send a player a notification about a new update.
     */
    private static final TemporalAmount NOTIFICATION_DELAY = Duration.ofHours(20);

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * The plugins {@link ConfigurationFile} for the debugging settings.
     */
    private final UpdaterConfig config;

    /**
     * The {@link UpdateSourceHandler} to get all available versions.
     */
    private final UpdateSourceHandler updateSourceHandler;

    /**
     * The {@link UpdateDownloader} to download new versions.
     */
    private final UpdateDownloader updateDownloader;

    /**
     * The {@link org.bukkit.plugin.Plugin} instance.
     */
    private final BetonQuest plugin;

    /**
     * The {@link BukkitScheduler} instance.
     */
    private final BukkitScheduler scheduler;

    /**
     * The {@link InstantSource} instance.
     */
    private final InstantSource instantSource;

    /**
     * The last timestamp, when a player was notified.
     */
    private final Map<UUID, Instant> lastNotification;

    /**
     * The latest version, the key is a {@link Version} and the value is the URL for the download.
     * If the URL is empty, the version is the current installed one or the already downloaded one.
     */
    private Pair<Version, String> latest;

    /**
     * The last timestamp, when an update was searched.
     */
    @Nullable
    private Instant lastCheck;

    /**
     * The update notification.
     */
    @Nullable
    private String updateNotification;

    /**
     * Create a new Updater instance.
     *
     * @param log                 the logger that will be used for logging
     * @param config              the config for the updater
     * @param currentVersion      the current version of the plugin
     * @param updateSourceHandler the {@link UpdateSourceHandler} to get all available versions
     * @param updateDownloader    the {@link UpdateDownloader} to download new versions
     * @param plugin              the {@link org.bukkit.plugin.Plugin} instance
     * @param scheduler           the {@link BukkitScheduler} instance
     * @param instantSource       the {@link InstantSource} instance
     */
    public Updater(final BetonQuestLogger log, final UpdaterConfig config, final Version currentVersion,
                   final UpdateSourceHandler updateSourceHandler, final UpdateDownloader updateDownloader,
                   final BetonQuest plugin, final BukkitScheduler scheduler, final InstantSource instantSource) {
        this.log = log;
        this.config = config;
        this.latest = Pair.of(currentVersion, null);
        this.updateSourceHandler = updateSourceHandler;
        this.updateDownloader = updateDownloader;
        this.plugin = plugin;
        this.scheduler = scheduler;
        this.instantSource = instantSource;
        this.lastNotification = new HashMap<>();

        search();
    }

    private String getUpdateNotification(final boolean automaticDownload) {
        final String version = "Found newer version '" + latest.getKey().getVersion() + "', ";
        final String automatic = " automatically installed on the next restart!";
        final String automaticProgress = "it will be downloaded and" + automatic;
        final String automaticDone = "it was downloaded and will be " + automatic;
        final String command = "it will be installed, if you execute '/q update'!";

        updateNotification = version + (automaticDownload ? automaticDone : command);
        return version + (automaticDownload ? automaticProgress : command);
    }

    /**
     * Starts an asynchronous search for updates.
     */
    public final void search() {
        config.reloadFromConfig();
        if (!config.isEnabled() || !shouldCheckVersion()) {
            return;
        }

        scheduler.runTaskAsynchronously(plugin, () -> {
            if (searchUpdate()) {
                final boolean automatic = config.isAutomatic();
                log.info(getUpdateNotification(automatic));
                if (automatic) {
                    update(null);
                }
            }
        });
    }

    private boolean searchUpdate() {
        final Pair<Version, String> newLatest = updateSourceHandler.searchUpdate(config, latest.getKey(), config.getDevIndicator());
        if (newLatest.getValue() == null) {
            return false;
        }
        latest = newLatest;
        return true;
    }

    private boolean shouldCheckVersion() {
        final Instant currentTime = instantSource.instant();
        if (lastCheck != null && lastCheck.plus(CHECK_DELAY).isAfter(currentTime)) {
            return false;
        }
        lastCheck = currentTime;
        return true;
    }

    /**
     * Return if a new version is available.
     *
     * @return True, if an update is available.
     */
    public boolean isUpdateAvailable() {
        return latest.getValue() != null;
    }

    /**
     * Return the new version string.
     *
     * @return The version string or null if there is no newer version.
     */
    @Nullable
    public String getUpdateVersion() {
        if (latest.getValue() != null) {
            return latest.getKey().getVersion();
        }
        return null;
    }

    /**
     * Sends an update notification to a player.
     *
     * @param player The player, that should receive the message.
     */
    public void sendUpdateNotification(final Player player) {
        if (config.isIngameNotification() && updateNotification != null) {
            final Instant currentTime = instantSource.instant();
            if (lastNotification.containsKey(player.getUniqueId()) && lastNotification.get(player.getUniqueId()).plus(NOTIFICATION_DELAY).isAfter(currentTime)) {
                return;
            }
            lastNotification.put(player.getUniqueId(), currentTime);

            player.sendMessage(plugin.getPluginTag() + ChatColor.DARK_GREEN + updateNotification);
        }
    }

    /**
     * Download the newest version of the plugin and store it in the /plugins/update/ folder.
     *
     * @param sender The {@link CommandSender} that should receive the update related messages.
     */
    public void update(@Nullable final CommandSender sender) {
        scheduler.runTaskAsynchronously(plugin, () -> {
            try {
                checkUpdateRequirements();

                sendMessage(sender, ChatColor.DARK_GREEN + "Started update to version '" + latest.getKey().getVersion() + "'...");
                executeUpdate();
                sendMessage(sender, ChatColor.DARK_GREEN + "...download finished. Restart the server to update the plugin.");
                updateNotification = "Update was downloaded! Restart the server to update the plugin.";
            } catch (final QuestException e) {
                sendMessage(sender, ChatColor.RED + e.getMessage());
                log.debug("Error while performing update!", e);
            }
        });
    }

    private void checkUpdateRequirements() throws QuestException {
        config.reloadFromConfig();
        if (!config.isEnabled()) {
            throw new QuestException("The updater is disabled! Change config entry 'update.enabled' to 'true' to enable it.");
        }
        if (searchUpdate()) {
            getUpdateNotification(config.isAutomatic());
            throw new QuestException("Update aborted! A newer version was found. New version '"
                    + getUpdateVersion() + "'! You can execute '/q update' again to update.");
        }
        if (latest.getValue() == null) {
            if (updateDownloader.alreadyDownloaded()) {
                throw new QuestException("The update was already downloaded! Restart the server to update the plugin.");
            }
            throw new QuestException("The updater did not find an update!"
                    + " This can depend on your update.strategy, check config entry 'update.strategy'.");
        }
    }

    private void executeUpdate() throws QuestException {
        try {
            updateDownloader.downloadToFile(new URL(latest.getValue()));
            latest = Pair.of(latest.getKey(), null);
        } catch (final MalformedURLException e) {
            throw new QuestException("There was an error resolving the url '" + latest.getValue() + "'! Reason: " + e.getMessage(), e);
        }
    }

    private void sendMessage(@Nullable final CommandSender sender, final String message) {
        log.info(ChatColor.stripColor(message));
        if (sender != null && !(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage(plugin.getPluginTag() + message);
        }
    }
}
