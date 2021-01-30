package org.betonquest.betonquest.utils.logger;

import lombok.CustomLog;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.utils.logger.custom.DebugLogFormatter;
import org.betonquest.betonquest.utils.logger.custom.HistoryHandler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.logging.FileHandler;

/**
 * Setup the log for the plugin.
 */
@CustomLog(topic = "LogWatcher")
public final class LogWatcher {
    /**
     * The path in the config for the debug state.
     */
    private static final String CONFIG_PATH = "debug";
    /**
     * The file of the latest log.
     */
    private static final File LOG_FILE = new File(BetonQuest.getInstance().getDataFolder(), "/logs/latest.log");
    /**
     * Is debugging enabled.
     */
    private boolean debugging;
    /**
     * The {@link HistoryHandler} that hold old LogRecords.
     */
    private HistoryHandler historyHandler;

    /**
     * Setup the debug log.
     */
    public LogWatcher() {
        setupDebugLogHandler();
    }

    private void setupDebugLogHandler() {
        try {
            renameDebugLogFile();

            final FileHandler fileHandler = new FileHandler(LOG_FILE.getAbsolutePath());
            fileHandler.setFormatter(new DebugLogFormatter());
            historyHandler = new HistoryHandler(fileHandler);
            historyHandler.setFilter((record) -> debugging);
            BetonQuest.getInstance().getLogger().addHandler(historyHandler);

            if (BetonQuest.getInstance().getConfig().getBoolean(CONFIG_PATH, true)) {
                startDebug();
            }
        } catch (final IOException e) {
            LOG.error(null, "It was not possible to crate the '" + LOG_FILE.getName() + "' or to register the plugin internal logger. "
                    + "This is not critical, the server can still run, but it is not possible to use the '/q debug true' command. "
                    + "Reason: " + e.getMessage(), e);
        }
    }

    /**
     * Start writing the latest.log file
     */
    public void startDebug() {
        synchronized (LogWatcher.class) {
            if (!debugging) {
                debugging = true;
                BetonQuest.getInstance().getConfig().set(CONFIG_PATH, true);
                BetonQuest.getInstance().saveConfig();
                historyHandler.push();
            }
        }
    }

    /**
     * Stop writing the latest.log file
     */
    public void endDebug() {
        synchronized (LogWatcher.class) {
            if (debugging) {
                debugging = false;
                BetonQuest.getInstance().getConfig().set(CONFIG_PATH, false);
                BetonQuest.getInstance().saveConfig();
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
        if (LOG_FILE.exists()) {
            if (LOG_FILE.length() == 0) {
                return;
            }
            final String newName = String.format("%1$ty-%1$tm-%1$td-%1$tH-%1$tM", new Date());
            final File newFile = new File(LOG_FILE.getParentFile(), newName + ".log");
            try {
                Files.move(LOG_FILE.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (final IOException e) {
                throw new IOException("Could not rename '" + LOG_FILE.getName() + "' file! Continue writing into the same log file.", e);
            }
        }
        if (!createDebugLogFile()) {
            throw new IOException("Could not create new '" + LOG_FILE.getName() + "' file!");
        }
    }

    private boolean createDebugLogFile() throws IOException {
        return (LOG_FILE.getParentFile().exists() || LOG_FILE.getParentFile().mkdirs()) && LOG_FILE.createNewFile();
    }
}
