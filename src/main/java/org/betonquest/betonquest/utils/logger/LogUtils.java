package org.betonquest.betonquest.utils.logger;

import lombok.CustomLog;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.utils.logger.custom.DebugLogFormatter;
import org.betonquest.betonquest.utils.logger.custom.LogHistory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Filter;

/**
 * Setup the log for the plugin.
 */
@CustomLog(topic = "LogUtils")
public final class LogUtils {
    /**
     * The file of the latest log
     */
    private static final File LOG_FILE = new File(BetonQuest.getInstance().getDataFolder(),
            "/logs/latest.log");
    /**
     * The {@link LogHistory}.
     */
    private static final LogHistory HISTORY = new LogHistory();
    /**
     * Is debugging enabled
     */
    private static boolean debugging;
    /**
     * The log {@link FileHandler}
     */
    private static FileHandler fileHandler;

    private LogUtils() {
    }

    /**
     * Start writing the latest.log file
     */
    public static void startDebug() {
        synchronized (LogUtils.class) {
            if (!debugging) {
                debugging = true;
                BetonQuest.getInstance().getConfig().set("debug", "true");
                BetonQuest.getInstance().saveConfig();
                HISTORY.writeHistory(fileHandler);
            }
        }
    }

    /**
     * Stop writing the latest.log file
     */
    public static void endDebug() {
        synchronized (LogUtils.class) {
            if (debugging) {
                debugging = false;
                BetonQuest.getInstance().getConfig().set("debug", "false");
                BetonQuest.getInstance().saveConfig();
            }
        }
    }

    /**
     * @return True, if debugging is enabled
     */
    public static boolean isDebugging() {
        return debugging;
    }

    /**
     * Create a latest.log file, rename the old latest.log and register the LogHandler
     */
    public static void setupLogger() {
        if (fileHandler != null) {
            LOG.warning(null, "The logger was already registered!");
            return;
        }

        try {
            renameLogFile();
            setupLoggerHandler();

            final boolean dataFolderExists = BetonQuest.getInstance().getDataFolder().exists();
            final String debugString = BetonQuest.getInstance().getConfig().getString("debug");
            final boolean debugReadError = debugString == null && !dataFolderExists;

            if (debugReadError) {
                LOG.warning(null, "It was not possible to read, if debugging is enabled. This enables debugging mode automatically.");
            }
            if (debugReadError || "true".equals(debugString)) {
                startDebug();
            }
        } catch (final IOException e) {
            LOG.warning(null, "It was not possible to crate the log file or to register the plugin internal logger. "
                            + "This is not critical, the server can still run, but it is not possible to use a 'debug log'.",
                    e);
        }
    }

    private static void setupLoggerHandler() throws IOException {
        fileHandler = new FileHandler(LOG_FILE.getAbsolutePath());
        BetonQuest.getInstance().getLogger().addHandler(fileHandler);
        fileHandler.setFormatter(new DebugLogFormatter());
        fileHandler.setFilter(getLogFilter());
    }

    private static Filter getLogFilter() {
        return (record) -> {
            if (debugging) {
                return true;
            }
            HISTORY.logToHistory(record);
            return false;
        };
    }

    private static void renameLogFile() throws IOException {
        if (fileHandler != null) {
            return;
        }
        if (!LOG_FILE.exists()) {
            createLogFile();
            return;
        }
        if (LOG_FILE.length() != 0) {
            final String newName = String.format("%1$ty-%1$tm-%1$td-%1$tH-%1$tM", new Date());
            final File newFile = new File(LOG_FILE.getParentFile(), newName + ".log");
            try {
                Files.move(LOG_FILE.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (final IOException e) {
                LOG.warning(null, "It was not possible to rename the 'debug log'. This means '" + LOG_FILE.getName()
                                + "' couldn't be renamed and writing to this file will be continued.",
                        e);
                return;
            }
            createLogFile();
        }
    }

    private static void createLogFile() throws IOException {
        LOG_FILE.getParentFile().mkdirs();
        LOG_FILE.createNewFile();
    }

}
