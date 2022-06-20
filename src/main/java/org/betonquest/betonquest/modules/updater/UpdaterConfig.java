package org.betonquest.betonquest.modules.updater;

import lombok.CustomLog;
import org.betonquest.betonquest.modules.versioning.UpdateStrategy;
import org.betonquest.betonquest.modules.versioning.Version;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Locale;

/**
 * Represents the config for the @{@link Updater}.
 */
@SuppressWarnings("PMD.DataClass")
@CustomLog
public class UpdaterConfig {
    private final static String DEV_SEPERATOR = "_";

    private final ConfigurationSection config;
    private final String devIndicator;
    private final Version current;

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
     * @return true if the updater is enabled
     */
    public boolean isEnabled() {
        return config.getBoolean("enabled", true);
    }

    /**
     * @return true if admins should be notified ingame
     */
    public boolean isIngameNotification() {
        return config.getBoolean("ingameNotification", true);
    }

    /**
     * @return the {@link UpdateStrategy}
     */
    public UpdateStrategy getStrategy() {
        final String updateStrategy = getUpdateStrategy();
        try {
            return UpdateStrategy.valueOf(updateStrategy);
        } catch (final IllegalArgumentException exception) {
            LOG.error("Could not parse 'update.strategy' in 'config.yml'!", exception);
            return UpdateStrategy.MINOR;
        }
    }

    /**
     * @return true if dev-versions should be downloaded
     */
    public boolean isDevDownloadEnabled() {
        return isForcedStrategy() || isDevUpdateStrategy();
    }

    /**
     * @return true if updates should be downloaded automatically
     */
    public boolean isAutomatic() {
        return !isForcedStrategy() && config.getBoolean("automatic", false);
    }

    /**
     * @return true if the {@link UpdateStrategy} forced by the plugin
     */
    public boolean isForcedStrategy() {
        return isCurrentVersionDev() ? !isDevUpdateStrategy() : current.hasQualifier();
    }

    private boolean isDevUpdateStrategy() {
        return loadUpdateStrategy().endsWith(DEV_SEPERATOR + devIndicator);
    }

    private String loadUpdateStrategy() {
        return config.getString("strategy", "MINOR").toUpperCase(Locale.ROOT);
    }

    private boolean isCurrentVersionDev() {
        return (devIndicator + "-").equals(current.getQualifier());
    }

    private String getUpdateStrategy() {
        final String updateStrategy = loadUpdateStrategy();
        if (isDevDownloadEnabled()) {
            return updateStrategy.substring(0, updateStrategy.length() - (this.devIndicator.length() + DEV_SEPERATOR.length()));
        }
        return updateStrategy;
    }
}
