package org.betonquest.betonquest.logger;

import org.betonquest.betonquest.api.config.ConfigurationFile;

import java.io.File;
import java.io.IOException;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Configuration facade for the debug {@link Handler} backed by a {@link ConfigurationFile}.
 */
public class DebugHandlerConfig {
    /**
     * Default value for the expire after minutes value.
     */
    private static final int EXPIRE_AFTER_DEFAULT = 10;

    /**
     * The file path to the latest.log.
     */
    private static final String LOG_FILE_PATH = "/latest.log";

    /**
     * The config path that holds all debug configuration settings..
     */
    private static final String CONFIG_SECTION = "debug";

    /**
     * The full path to the config setting, that saved if debugging is enabled.
     */
    private static final String CONFIG_ENABLED_PATH = CONFIG_SECTION + ".enabled";

    /**
     * The full path to the config setting, that defined the history expiration time in minutes.
     */
    private static final String CONFIG_HISTORY_PATH = CONFIG_SECTION + ".history_in_minutes";

    /**
     * The {@link ConfigurationFile} where to configure debugging.
     */
    private final ConfigurationFile config;

    /**
     * The {@link File} where to log logger messages.
     */
    private final File logFile;

    /**
     * Wrap the given {@link ConfigurationFile} to easily access the relevant options for the debug {@link Handler}.
     *
     * @param config        the related {@link ConfigurationFile}
     * @param logFileFolder the folder where to write the logfile.
     */
    public DebugHandlerConfig(final ConfigurationFile config, final File logFileFolder) {
        this.config = config;
        this.logFile = new File(logFileFolder, LOG_FILE_PATH);
    }

    /**
     * Get logging state.
     *
     * @return true if debugging is enabled in the config; false otherwise
     */
    public boolean isDebugging() {
        return config.getBoolean(CONFIG_ENABLED_PATH, false);
    }

    /**
     * Set logging state.
     *
     * @param debugging enabled state to set
     * @throws IOException when persisting the changed state fails
     */
    public void setDebugging(final boolean debugging) throws IOException {
        if (!config.isBoolean(CONFIG_ENABLED_PATH) || config.getBoolean(CONFIG_ENABLED_PATH) != debugging) {
            config.set(CONFIG_ENABLED_PATH, debugging);
            config.save();
        }
    }

    /**
     * Gets how long {@link LogRecord}s stay in a cache.
     * <p>
     * If it is 0, no history will be saved at all.
     *
     * @return duration until expiration in minutes
     */
    public int getExpireAfterMinutes() {
        return config.getInt(CONFIG_HISTORY_PATH, EXPIRE_AFTER_DEFAULT);
    }

    /**
     * Get the {@link File} for writing log messages into.
     *
     * @return the {@link File} to log into
     */
    public File getLogFile() {
        return logFile;
    }
}
