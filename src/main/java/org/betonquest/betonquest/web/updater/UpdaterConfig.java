package org.betonquest.betonquest.web.updater;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.versioning.UpdateStrategy;
import org.betonquest.betonquest.versioning.Version;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Locale;

/**
 * Represents the config for the @{@link Updater}.
 */
@SuppressWarnings("PMD.DataClass")
public class UpdaterConfig {
    /**
     * The string prefix of the path to the settings in the given {@link ConfigurationSection}
     */
    private static final String UPDATE_SECTION = "update.";

    /**
     * The separator between the {@link UpdateStrategy} and a given dev indicator
     */
    private static final String DEV_SEPARATOR = "_";

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * The configuration section that contains the `update` section for all settings
     */
    private final ConfigurationSection config;

    /**
     * The indicator for a dev version
     */
    private final String devIndicator;

    /**
     * The current installed version
     */
    private final Version current;

    /**
     * True if the updater is enabled
     */
    private boolean enabled;

    /**
     * True if ingame notifications are enabled
     */
    private boolean ingameNotification;

    /**
     * True if an automatic updates are enabled
     */
    private boolean automatic;

    /**
     * The configured {@link UpdateStrategy}
     */
    private UpdateStrategy strategy = UpdateStrategy.MINOR;

    /**
     * True if the update for development versions are enabled
     */
    private boolean devDownloadEnabled;

    /**
     * Reads the configuration file.
     *
     * @param log          the logger that will be used for logging
     * @param config       the {@link ConfigurationSection} that contains the updater settings
     * @param current      the current {@link Version} of the plugin
     * @param devIndicator the string qualifier that represents dev versions
     */
    public UpdaterConfig(final BetonQuestLogger log, final ConfigurationSection config, final Version current, final String devIndicator) {
        this.log = log;
        this.config = config;
        this.current = current;
        this.devIndicator = devIndicator.toUpperCase(Locale.ROOT);
    }

    /**
     * Reload the settings from the config file.
     */
    public void reloadFromConfig() {
        enabled = config.getBoolean(UPDATE_SECTION + "enabled", true);
        ingameNotification = config.getBoolean(UPDATE_SECTION + "ingameNotification", true);
        automatic = config.getBoolean(UPDATE_SECTION + "automatic", false);

        String updateStrategyRaw = config.getString(UPDATE_SECTION + "strategy", null);
        updateStrategyRaw = updateStrategyRaw == null ? "MINOR" : updateStrategyRaw.toUpperCase(Locale.ROOT);
        final String updateStrategy = getUpdateStrategy(updateStrategyRaw);
        devDownloadEnabled = !updateStrategyRaw.equals(updateStrategy);
        try {
            strategy = UpdateStrategy.valueOf(updateStrategy);
        } catch (final IllegalArgumentException exception) {
            log.error("Could not parse 'update.strategy' in 'config.yml'!", exception);
        }
    }

    private String getUpdateStrategy(final String updateStrategy) {
        if (updateStrategy.endsWith(DEV_SEPARATOR + devIndicator)) {
            return updateStrategy.substring(0, updateStrategy.length() - (this.devIndicator.length() + DEV_SEPARATOR.length()));
        }
        return updateStrategy;
    }

    /**
     * @return true if the updater is enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * @return true if admins should be notified ingame
     */
    public boolean isIngameNotification() {
        return ingameNotification;
    }

    /**
     * @return the {@link UpdateStrategy}
     */
    public UpdateStrategy getStrategy() {
        return strategy;
    }

    public String getDevIndicator() {
        return devIndicator;
    }

    /**
     * @return true if dev-versions should be downloaded
     */
    public boolean isDevDownloadEnabled() {
        return isForcedStrategy() || devDownloadEnabled;
    }

    /**
     * @return true if updates should be downloaded automatically
     */
    public boolean isAutomatic() {
        return !isForcedStrategy() && automatic;
    }

    /**
     * @return true if the {@link UpdateStrategy} forced by the plugin
     */
    public boolean isForcedStrategy() {
        return isCurrentVersionDev() ? !devDownloadEnabled : current.hasQualifier();
    }

    private boolean isCurrentVersionDev() {
        return (devIndicator + "-").equals(current.getQualifier());
    }
}
