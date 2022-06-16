package org.betonquest.betonquest.modules.logger;

import lombok.CustomLog;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.betonquest.betonquest.api.config.ConfigurationFile;
import org.betonquest.betonquest.modules.logger.format.ChatFormatter;
import org.betonquest.betonquest.modules.logger.format.LogfileFormatter;
import org.betonquest.betonquest.modules.logger.handler.LazyHandler;
import org.betonquest.betonquest.modules.logger.handler.ResettableHandler;
import org.betonquest.betonquest.modules.logger.handler.chat.BukkitChatHandler;
import org.betonquest.betonquest.modules.logger.handler.chat.RecordReceiverSelector;
import org.betonquest.betonquest.modules.logger.handler.history.BukkitSchedulerCleaningLogQueue;
import org.betonquest.betonquest.modules.logger.handler.history.DiscardingLogQueue;
import org.betonquest.betonquest.modules.logger.handler.history.HistoryHandler;
import org.betonquest.betonquest.modules.logger.handler.history.HistoryHandlerConfig;
import org.betonquest.betonquest.modules.logger.handler.history.LogRecordQueue;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.Duration;
import java.time.Instant;
import java.time.InstantSource;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Handler;

/**
 * A static helper class to create {@link Handler}s in the way they are usually used by BetonQuest.
 */
@CustomLog(topic = "LogWatcherFactory")
public final class HandlerFactory {
    private HandlerFactory() {
        // Empty
    }

    /**
     * Create a {@link BukkitChatHandler}.
     *
     * @param plugin the main plugin
     * @param receiverSelector the receiver selector
     * @param bukkitAudiences the audience provider
     * @return a new {@link BukkitChatHandler}
     */
    public static BukkitChatHandler createChatHandler(final Plugin plugin, final RecordReceiverSelector receiverSelector, final BukkitAudiences bukkitAudiences) {
        final BukkitChatHandler handler = new BukkitChatHandler(receiverSelector, bukkitAudiences);
        handler.setFormatter(new ChatFormatter(ChatFormatter.PluginDisplayMethod.ROOT_PLUGIN_AND_PLUGIN, plugin, "BQ"));
        return handler;
    }

    /**
     * Create a {@link HistoryHandler}.
     *
     * @param plugin {@link Plugin} instance
     * @param scheduler {@link BukkitScheduler} instance
     * @param config {@link ConfigurationFile} instance
     * @param logFileFolder {@link File} to the log folder
     * @param instantSource {@link InstantSource} instance
     * @return a new {@link HistoryHandler}
     */
    public static HistoryHandler createHistoryHandler(final Plugin plugin, final BukkitScheduler scheduler, final ConfigurationFile config, final File logFileFolder, final InstantSource instantSource) {
        final HistoryHandlerConfig historyHandlerConfig = new HistoryHandlerConfig(config, logFileFolder);
        final LogRecordQueue logQueue = createLogRecordQueue(plugin, scheduler, instantSource, historyHandlerConfig.getExpireAfterMinutes());
        final ResettableHandler targetHandler = createDebugLogFileHandler(historyHandlerConfig.getLogFile(), instantSource);
        return new HistoryHandler(historyHandlerConfig, logQueue, targetHandler);
    }

    private static ResettableHandler createDebugLogFileHandler(final File logFile, final InstantSource instantSource) {
        return new ResettableHandler(() -> new LazyHandler(() -> setupFileHandler(logFile, instantSource)));
    }

    private static LogRecordQueue createLogRecordQueue(final Plugin plugin, final BukkitScheduler scheduler, final InstantSource instantSource, final int keepMinutes) {
        if (keepMinutes == 0) {
            return new DiscardingLogQueue();
        } else {
            final BukkitSchedulerCleaningLogQueue bukkitQueue = new BukkitSchedulerCleaningLogQueue(instantSource, Duration.of(keepMinutes, ChronoUnit.MINUTES));
            bukkitQueue.runCleanupTimerAsynchronously(scheduler, plugin, 20, 20);
            return bukkitQueue;
        }
    }

    private static Handler setupFileHandler(final File logFile, final InstantSource instantSource) {
        try {
            renameLogFile(logFile, instantSource);
            final FileHandler fileHandler = new FileHandler(logFile.getAbsolutePath());
            fileHandler.setFormatter(new LogfileFormatter());
            return fileHandler;
        } catch (final IOException e) {
            LOG.error("It was not possible to create the '" + logFile.getName() + "' or to register the plugin's internal logger. "
                    + "This is not a critical error, the server can still run, but it is not possible to use the '/q debug true' command. "
                    + "Reason: " + e.getMessage(), e);
            return null;
        }
    }

    private static void renameLogFile(final File logFile, final InstantSource instantSource) throws IOException {
        if (logFile.exists()) {
            final String newName = getFileCreationTime(logFile);
            final File newFile = getNewLogFile(logFile.getParentFile(), newName, 1);
            try {
                Files.move(logFile.toPath(), newFile.toPath());
            } catch (final IOException e) {
                throw new IOException("Could not rename '" + logFile.getName() + "' file! Continue writing into the same log file.", e);
            }
        }
        if (!createFolderAndFile(logFile)) {
            throw new IOException("Could not create new '" + logFile.getName() + "' file!");
        }
        setFileCreationTime(logFile, instantSource.instant());
    }

    private static String getFileCreationTime(final File logFile) throws IOException {
        try {
            final BasicFileAttributes attr = Files.readAttributes(logFile.toPath(), BasicFileAttributes.class);
            final FileTime fileTime = attr.creationTime();
            return String.format("%1$ty-%1$tm-%1$td-%1$tH-%1$tM", Date.from(fileTime.toInstant()));
        } catch (final IOException e) {
            throw new IOException("Could not get file attributes for '" + logFile.getName() + "' file! Reason: " + e.getMessage(), e);
        }
    }

    private static void setFileCreationTime(final File logFile, final Instant creationTime) throws IOException {
        final BasicFileAttributeView attributes = Files.getFileAttributeView(logFile.toPath(), BasicFileAttributeView.class);
        final FileTime time = FileTime.from(creationTime);
        attributes.setTimes(time, time, time);
    }

    private static File getNewLogFile(final File parentFile, final String newName, final int offset) {
        final File newFile = new File(parentFile, newName + "-" + offset + ".log");
        if (newFile.exists()) {
            return getNewLogFile(parentFile, newName, offset + 1);
        }
        return newFile;
    }

    private static boolean createFolderAndFile(final File logFile) throws IOException {
        return (logFile.getParentFile().exists() || logFile.getParentFile().mkdirs()) && logFile.createNewFile();
    }
}
