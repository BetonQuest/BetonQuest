package org.betonquest.betonquest.modules.logger.custom.debug.config;

import org.betonquest.betonquest.api.config.ConfigurationFile;

import java.io.File;
import java.io.IOException;

/**
 * This is a debug configuration based on a {@link ConfigurationFile}.
 */
public class FileDebugConfig extends SimpleDebugConfig {
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
     * The full path to the config setting, that defined the history expiration time in minutes,
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
     * Create a new {@link DebugConfig} that is based on a {@link ConfigurationFile}.
     *
     * @param config        the related {@link ConfigurationFile}
     * @param logFileFolder the folder where to write the logfile.
     */
    public FileDebugConfig(final ConfigurationFile config, final File logFileFolder) {
        super(config.getBoolean(CONFIG_ENABLED_PATH, false));
        this.config = config;
        this.logFile = new File(logFileFolder, LOG_FILE_PATH);
    }

    @Override
    public void startDebug() throws IOException {
        super.startDebug();
        saveDebuggingToConfig();
    }

    @Override
    public void stopDebug() throws IOException {
        super.stopDebug();
        saveDebuggingToConfig();
    }

    @Override
    public int getExpireAfterMinutes() {
        return config.getInt(CONFIG_HISTORY_PATH, super.getExpireAfterMinutes());
    }

    /**
     * Saves the current debugging state to the configuration file.
     *
     * @throws IOException Is thrown if the configuration file could not be saved
     */
    private void saveDebuggingToConfig() throws IOException {
        if (!config.isBoolean(CONFIG_ENABLED_PATH) || config.getBoolean(CONFIG_ENABLED_PATH) != isDebugging()) {
            config.set(CONFIG_ENABLED_PATH, isDebugging());
            config.save();
        }
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
