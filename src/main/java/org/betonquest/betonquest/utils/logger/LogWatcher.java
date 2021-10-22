package org.betonquest.betonquest.utils.logger;

import lombok.CustomLog;
import org.betonquest.betonquest.utils.logger.custom.ChatLogFormatter;
import org.betonquest.betonquest.utils.logger.custom.DebugLogFormatter;
import org.betonquest.betonquest.utils.logger.custom.HistoryLogHandler;
import org.betonquest.betonquest.utils.logger.custom.PlayerLogHandler;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.FileHandler;
import java.util.logging.Level;

/**
 * Setups the log for the plugin.
 */
@CustomLog(topic = "LogWatcher")
public final class LogWatcher {
    /**
     * The config path that holds the debug state.
     */
    private static final String CONFIG_PATH = "debug";
    /**
     * The file path where the latest.log is located.
     */
    private static final String LOG_FILE_PATH = "/logs/latest.log";

    /**
     * The related JavaPlugin for that LogWatcher.
     */
    private final Plugin plugin;
    /**
     * The file of the latest log.
     */
    private final File logFile;
    /**
     * The {@link HistoryLogHandler} that holds old LogRecords.
     */
    private final HistoryLogHandler historyHandler;
    /**
     * All active log filters for the in-game log.
     */
    private final Map<UUID, Map<String, Level>> playerFilters;
    /**
     * Whether debugging is enabled.
     */
    private boolean debugging;

    /**
     * Setups the debug and in-game chat log.
     *
     * @param plugin The related {@link Plugin} instance
     */
    public LogWatcher(final Plugin plugin) {
        this.plugin = plugin;
        this.logFile = new File(plugin.getDataFolder(), LOG_FILE_PATH);
        playerFilters = new HashMap<>();
        historyHandler = setupDebugLogHandler();
        setupPlayerLogHandler();

        if (historyHandler != null && plugin.getConfig().getBoolean(CONFIG_PATH, false)) {
            startDebug();
        }
    }

    private HistoryLogHandler setupDebugLogHandler() {
        try {
            renameDebugLogFile();

            final FileHandler fileHandler = new FileHandler(logFile.getAbsolutePath());
            fileHandler.setFormatter(new DebugLogFormatter());
            final HistoryLogHandler historyHandler = new HistoryLogHandler(fileHandler);
            historyHandler.setFilter((record) -> debugging);
            plugin.getLogger().addHandler(historyHandler);
            return historyHandler;
        } catch (final IOException e) {
            LOG.error("It was not possible to create the '" + logFile.getName() + "' or to register the plugin's internal logger. "
                    + "This is not a critical error, the server can still run, but it is not possible to use the '/q debug true' command. "
                    + "Reason: " + e.getMessage(), e);
        }
        return null;
    }

    private void setupPlayerLogHandler() {
        final PlayerLogHandler playerHandler = new PlayerLogHandler(playerFilters);
        playerHandler.setFormatter(new ChatLogFormatter());
        plugin.getLogger().addHandler(playerHandler);
    }

    /**
     * Starts writing the latest.log file.
     */
    public void startDebug() {
        synchronized (LogWatcher.class) {
            if (!debugging) {
                debugging = true;
                plugin.getConfig().set(CONFIG_PATH, true);
                plugin.saveConfig();
                historyHandler.push();
            }
        }
    }

    /**
     * Stops writing the latest.log file.
     */
    public void endDebug() {
        synchronized (LogWatcher.class) {
            if (debugging) {
                debugging = false;
                plugin.getConfig().set(CONFIG_PATH, false);
                plugin.saveConfig();
            }
        }
    }

    /**
     * @return True, if debugging is enabled
     */
    public boolean isDebugging() {
        return debugging;
    }

    private void renameDebugLogFile() throws IOException {
        if (logFile.exists()) {
            if (logFile.length() == 0) {
                return;
            }
            final String newName = String.format("%1$ty-%1$tm-%1$td-%1$tH-%1$tM", new Date());
            final File newFile = new File(logFile.getParentFile(), newName + ".log");
            try {
                Files.move(logFile.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (final IOException e) {
                throw new IOException("Could not rename '" + logFile.getName() + "' file! Continue writing into the same log file.", e);
            }
        }
        if (!createDebugLogFile()) {
            throw new IOException("Could not create new '" + logFile.getName() + "' file!");
        }
    }

    private boolean createDebugLogFile() throws IOException {
        return (logFile.getParentFile().exists() || logFile.getParentFile().mkdirs()) && logFile.createNewFile();
    }

    /**
     * Adds a filter to a player.
     *
     * @param uuid   The {@link UUID} of the player
     * @param filter The filter pattern
     * @param level  The Level of the filter
     * @return True if the filter was successfully added
     */
    public boolean addFilter(final UUID uuid, final String filter, final Level level) {
        if (!playerFilters.containsKey(uuid)) {
            playerFilters.put(uuid, new HashMap<>());
        }
        final Map<String, Level> filters = playerFilters.get(uuid);
        if (filters.containsKey(filter) && filters.get(filter).equals(level)) {
            return false;
        }
        filters.put(filter, level);
        return true;
    }

    /**
     * Removes a filter from a player.
     *
     * @param uuid   The {@link UUID} of the player
     * @param filter The filter pattern
     * @return True if the filter was successfully removed
     */
    public boolean removeFilter(final UUID uuid, final String filter) {
        if (playerFilters.containsKey(uuid)) {
            final boolean removed = playerFilters.get(uuid).remove(filter) != null;
            if (playerFilters.get(uuid).isEmpty()) {
                playerFilters.remove(uuid);
            }
            return removed;
        }
        return false;
    }

    /**
     * Gets a players filters.
     *
     * @param uuid The {@link UUID} of the player
     * @return A list of filters
     */
    public List<String> getFilters(final UUID uuid) {
        if (playerFilters.containsKey(uuid)) {
            return new ArrayList<>(playerFilters.get(uuid).keySet());
        }
        return new ArrayList<>();
    }
}
