package org.betonquest.betonquest.modules.updater;

import lombok.CustomLog;
import org.betonquest.betonquest.modules.versioning.UpdateStrategy;
import org.betonquest.betonquest.modules.versioning.Version;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

/**
 * Represents the config for the @{@link Updater}.
 */
@SuppressWarnings("PMD.DataClass")
@CustomLog
public class UpdaterConfig {
    private static final String UPDATE_SECTION = "update.";
    private final static String DEV_SEPERATOR = "_";
    private final ConfigurationSection config;
    private final String devIndicator;
    private final Version current;
    private boolean enabled;
    private boolean ingameNotification;
    private boolean automatic;
    private UpdateStrategy strategy = UpdateStrategy.MINOR;
    private boolean devDownloadEnabled;

    /**
     * Reads the configuration file.
     *
     * @param config       the {@link ConfigurationSection} that contains the updater settings
     * @param current      the current {@link Version} of the plugin
     * @param devIndicator the string qualifier that represents dev versions
     */
    public UpdaterConfig(final ConfigurationSection config, final Version current, final String devIndicator) {
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

        final String updateStrategyRaw = config.getString(UPDATE_SECTION + "strategy", "MINOR").toUpperCase(Locale.ROOT);
        final String updateStrategy = getUpdateStrategy(updateStrategyRaw);
        devDownloadEnabled = !updateStrategyRaw.equals(updateStrategy);
        try {
            strategy = UpdateStrategy.valueOf(updateStrategy);
        } catch (final IllegalArgumentException exception) {
            LOG.error("Could not parse 'update.strategy' in 'config.yml'!", exception);
        }
    }

    private String getUpdateStrategy(final String updateStrategy) {
        if (updateStrategy.endsWith(DEV_SEPERATOR + devIndicator)) {
            return updateStrategy.substring(0, updateStrategy.length() - (this.devIndicator.length() + DEV_SEPERATOR.length()));
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
    public @NotNull UpdateStrategy getStrategy() {
        return strategy;
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
