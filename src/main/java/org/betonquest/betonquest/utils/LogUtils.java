package org.betonquest.betonquest.utils;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.CustomLog;
import org.betonquest.betonquest.BetonQuest;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.*;

/**
 * Setup the log for the plugin.
 */
@CustomLog
public final class LogUtils {
    /**
     * The file of the latest log
     */
    private static final File LOG_FILE = new File(BetonQuest.getInstance().getDataFolder(),
            "/logs/latest.log");

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
                History.writeHistory();
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
    @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
    public static void setupLogger() {
        if (fileHandler != null) {
            LOG.warning("The logger was already registered!");
            return;
        }
        BetonQuest.getInstance().getLogger().setLevel(Level.ALL);

        try {
            renameLogFile();
            setupLoggerHandler();

            final boolean dataFolderExists = BetonQuest.getInstance().getDataFolder().exists();
            final String debugString = BetonQuest.getInstance().getConfig().getString("debug");
            final boolean debugReadError = debugString == null && !dataFolderExists;

            if (debugReadError) {
                LOG.warning("It was not possible to read, if debugging is enabled. This enables debugging mode automatically.");
            }
            if (debugReadError || "true".equals(debugString)) {
                startDebug();
            }
        } catch (final IOException e) {
            LOG.warning("It was not possible to crate the log file or to register the plugin internal logger. "
                            + "This is not critical, the server can still run, but it is not possible to use a 'debug log'.",
                    e);
        }
    }

    private static void setupLoggerHandler()
            throws IOException {
        fileHandler = new FileHandler(LOG_FILE.getAbsolutePath());
        BetonQuest.getInstance().getLogger().addHandler(fileHandler);
        fileHandler.setFormatter(new LogFormatter());
        fileHandler.setFilter(getLogFilter());
    }

    private static Filter getLogFilter() {
        return (record) -> {
            if (debugging) {
                return true;
            }
            History.logToHistory(record);
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
                LOG.warning("It was not possible to rename the 'debug log'. This means '" + LOG_FILE.getName()
                                + "' couldn't be renamed and writing to this file will be continued.",
                        e);
                return;
            }
            createLogFile();
            LOG.info("A new log file was created, and the old one was renamed to '" + newFile.getName() + "'.");
        }
    }

    @SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_BAD_PRACTICE")
    private static void createLogFile() throws IOException {
        LOG_FILE.getParentFile().mkdirs();
        LOG_FILE.createNewFile();
    }

    /**
     * A history of {@link LogRecord}
     */
    private static class History {

        /**
         * The size of the log history
         */
        private static final int SIZE = 1000;
        /**
         * The log history
         */
        private static final LogRecord[] RECORDS = new LogRecord[SIZE];
        /**
         * The index of the current history position
         */
        private static int index;

        private static void writeHistory() {
            synchronized (History.class) {
                int currentIndex = index;
                boolean hasHistory = false;
                do {
                    if (RECORDS[currentIndex] != null) {
                        if (!hasHistory) {
                            fileHandler.publish(new LogRecord(Level.INFO, "=====START OF HISTORY====="));
                            hasHistory = true;
                        }
                        fileHandler.publish(RECORDS[currentIndex]);
                    }
                    currentIndex++;
                    if (currentIndex == SIZE) {
                        currentIndex = 0;
                    }
                } while (currentIndex != index);
                if (hasHistory) {
                    fileHandler.publish(new LogRecord(Level.INFO, "=====END OF HISTORY====="));
                }
                Arrays.fill(RECORDS, null);
                index = 0;
            }
        }

        private static void logToHistory(final LogRecord record) {
            synchronized (History.class) {
                if (index == SIZE) {
                    index = 0;
                }
                RECORDS[index] = record;
                index++;
            }
        }
    }

    /**
     * This is a simple log formatting class
     */
    private static class LogFormatter extends Formatter {

        /**
         * The date, to print the time for a log report
         */
        private final Date dat = new Date();

        @Override
        public String format(final LogRecord record) {
            dat.setTime(record.getMillis());
            final String message = formatMessage(record);
            final String throwable = formatThrowable(record);

            return String.format("[%1$ty.%1$tm.%1$td %tT %2$s]: %3$s%4$s%n",
                    dat,
                    record.getLevel().getName(),
                    message,
                    throwable);
        }

        private String formatThrowable(final LogRecord record) {
            String throwable = "";
            if (record.getThrown() != null) {
                final StringWriter sWriter = new StringWriter();
                final PrintWriter pWriter = new PrintWriter(sWriter);
                pWriter.println();
                record.getThrown().printStackTrace(pWriter);
                pWriter.close();
                throwable = sWriter.toString();
            }
            return throwable;
        }
    }
}
