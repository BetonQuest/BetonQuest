package org.betonquest.betonquest.modules.logger;

import lombok.CustomLog;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.betonquest.betonquest.api.config.ConfigurationFile;
import org.betonquest.betonquest.modules.logger.custom.chat.ChatHandler;
import org.betonquest.betonquest.modules.logger.custom.chat.PlayerFilter;
import org.betonquest.betonquest.modules.logger.custom.chat.PlayerPackageFilter;
import org.betonquest.betonquest.modules.logger.custom.debug.HistoryHandler;
import org.betonquest.betonquest.modules.logger.custom.debug.HistoryHandlerConfig;
import org.betonquest.betonquest.modules.logger.format.ChatFormatter;
import org.betonquest.betonquest.modules.logger.format.LogfileFormatter;
import org.betonquest.betonquest.modules.logger.handler.LazyLogHandler;
import org.betonquest.betonquest.modules.logger.handler.ResettableLogHandler;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.time.InstantSource;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Handler;

/**
 * A factory to create instances for the creation of a {@link LogWatcher}.
 */
@CustomLog(topic = "LogWatcherFactory")
public final class LogWatcherFactory {
    private LogWatcherFactory() {
        // Empty
    }

    /**
     * Create a new {@link ChatHandler} with the related instances.
     *
     * @param plugin          {@link Plugin} instance
     * @param bukkitAudiences {@link BukkitAudiences} instance
     * @return a new {@link ChatHandler}
     */
    public static ChatHandler createChatHandler(final Plugin plugin, final BukkitAudiences bukkitAudiences) {
        final PlayerFilter playerFilter = new PlayerPackageFilter();
        final ChatHandler handler = new ChatHandler(playerFilter, bukkitAudiences);
        handler.setFormatter(new ChatFormatter(ChatFormatter.PluginDisplayMethod.ROOT_PLUGIN_AND_PLUGIN, plugin, "BQ"));
        return handler;
    }

    /**
     * Create a new {@link HistoryHandler} with the related instances.
     *
     * @param plugin        {@link Plugin} instance
     * @param scheduler     {@link BukkitScheduler} instance
     * @param config        {@link ConfigurationFile} instance
     * @param logFileFolder {@link File} to the log folder
     * @param instantSource {@link InstantSource} instance
     * @return a new {@link HistoryHandler}
     */
    public static HistoryHandler createHistoryHandler(final Plugin plugin, final BukkitScheduler scheduler, final ConfigurationFile config, final File logFileFolder, final InstantSource instantSource) {
        final HistoryHandlerConfig historyHandlerConfig = new HistoryHandlerConfig(config, logFileFolder);
        final ResettableLogHandler targetHandler = new ResettableLogHandler(
                () -> new LazyLogHandler(() -> setupFileHandler(historyHandlerConfig, instantSource))
        );
        return new HistoryHandler(historyHandlerConfig, plugin, scheduler, targetHandler, instantSource);
    }

    private static Handler setupFileHandler(final HistoryHandlerConfig historyHandlerConfig, final InstantSource instantSource) {
        try {
            renameLogFile(historyHandlerConfig.getLogFile(), instantSource);
            final FileHandler fileHandler = new FileHandler(historyHandlerConfig.getLogFile().getAbsolutePath());
            fileHandler.setFormatter(new LogfileFormatter());
            return fileHandler;
        } catch (final IOException e) {
            LOG.error("It was not possible to create the '" + historyHandlerConfig.getLogFile().getName() + "' or to register the plugin's internal logger. "
                    + "This is not a critical error, the server can still run, but it is not possible to use the '/q debug true' command. "
                    + "Reason: " + e.getMessage(), e);
        }
        return null;
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
