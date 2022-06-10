package org.betonquest.betonquest.modules.updater;

import lombok.CustomLog;
import org.betonquest.betonquest.api.config.ConfigurationFile;
import org.betonquest.betonquest.modules.versioning.UpdateStrategy;
import org.betonquest.betonquest.modules.versioning.Version;

import java.util.Locale;

/**
 * Represents the config for the @{@link Updater}.
 */
@SuppressWarnings("PMD.DataClass")
@CustomLog
public class UpdaterConfig {
    /**
     * True, if the updater is enabled.
     */
    private final boolean enabled;
    /**
     * The selected {@link UpdateStrategy}.
     */
    private final UpdateStrategy strategy;
    /**
     * True if dev versions should be downloaded.
     */
    private final boolean devDownloadEnabled;
    /**
     * True, if updated should be downloaded automatic.
     */
    private final boolean automatic;
    /**
     * Should the player be notified ingame.
     */
    private final boolean ingameNotification;
    /**
     * True, if the {@link UpdateStrategy} was forced to a DEV UpdateStrategy.
     */
    private final boolean forcedStrategy;

    /**
     * Reads the configuration file.
     *
     * @param config       the related configuration file
     * @param current      the current {@link Version} of the plugin
     * @param devIndicator the string qualifier that represents dev versions
     */
    public UpdaterConfig(final ConfigurationFile config, final Version current, final String devIndicator) {
        enabled = config.getBoolean("update.enabled", true);
        ingameNotification = config.getBoolean("update.ingameNotification", true);

        String updateStrategy = config.getString("update.strategy", "MINOR").toUpperCase(Locale.ROOT);
        final String devIndicatorSuffix = "_" + devIndicator;
        boolean devDownloadEnabled = updateStrategy.endsWith(devIndicatorSuffix);
        if (devDownloadEnabled) {
            updateStrategy = updateStrategy.substring(0, updateStrategy.length() - (devIndicatorSuffix.length()));
        }
        UpdateStrategy strategy;
        try {
            strategy = UpdateStrategy.valueOf(updateStrategy);
        } catch (final IllegalArgumentException exception) {
            LOG.error("Could not parse 'update.strategy' in 'config.yml'!", exception);
            strategy = UpdateStrategy.MINOR;
        }
        this.strategy = strategy;

        final boolean isDev = (devIndicator + "-").equals(current.getQualifier());
        if (isDev && !devDownloadEnabled || !isDev && current.hasQualifier()) {
            devDownloadEnabled = true;
            automatic = false;
            forcedStrategy = true;
        } else {
            automatic = config.getBoolean("update.automatic", false);
            forcedStrategy = false;
        }
        this.devDownloadEnabled = devDownloadEnabled;
    }

    /**
     * @return true if the updater is enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * @return the {@link UpdateStrategy}
     */
    public UpdateStrategy getStrategy() {
        return strategy;
    }

    /**
     * @return true if dev-versions should be downloaded
     */
    public boolean isDevDownloadEnabled() {
        return devDownloadEnabled;
    }

    /**
     * @return true if updates should be downloaded automatically
     */
    public boolean isAutomatic() {
        return automatic;
    }

    /**
     * @return true if admins should be notified ingame
     */
    public boolean isIngameNotification() {
        return ingameNotification;
    }

    /**
     * @return true if the {@link UpdateStrategy} forced by the plugin
     */
    public boolean isForcedStrategy() {
        return forcedStrategy;
    }
}
