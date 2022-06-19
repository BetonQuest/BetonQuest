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

    private final ConfigurationSection config;
    private final String devIndicator;
    private final Version current;

    /**
     * Reads the configuration file.
     *
     * @param config       the related configuration file
     * @param current      the current {@link Version} of the plugin
     * @param devIndicator the string qualifier that represents dev versions
     */
    public UpdaterConfig(final ConfigurationSection config, final Version current, final String devIndicator) {
        this.config = config;
        this.current = current;
        this.devIndicator = "_" + devIndicator.toUpperCase(Locale.ROOT);
    }

    private boolean isCurrentDev() {
        return (devIndicator + "-").equals(current.getQualifier());
    }

    private boolean isForced() {
        return isDevForced() || isCustomBuild();
    }

    private boolean isDevForced() {
        return isCurrentDev() && !isDevStrategy();
    }

    private boolean isCustomBuild() {
        return !isDevStrategy() && current.hasQualifier();
    }

    private String getUpdateStrategy() {
        final String updateStrategy = loadUpdateStrategy();
        if (isDevDownloadEnabled()) {
            return updateStrategy.substring(0, updateStrategy.length() - (this.devIndicator.length()));
        }
        return updateStrategy;
    }

    /**
     * @return true if the updater is enabled
     */
    public boolean isEnabled() {
        return config.getBoolean("update.enabled", true);
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
        return isForced() || isDevStrategy();
    }

    private boolean isDevStrategy() {
        return loadUpdateStrategy().endsWith(devIndicator);
    }

    /**
     * @return true if updates should be downloaded automatically
     */
    public boolean isAutomatic() {
        return !isForced() && loadAutomatic();
    }

    /**
     * @return true if admins should be notified ingame
     */
    public boolean isIngameNotification() {
        return loadIngameNotification();
    }

    /**
     * @return true if the {@link UpdateStrategy} forced by the plugin
     */
    public boolean isForcedStrategy() {
        return isForced();
    }

    private boolean loadIngameNotification() {
        return config.getBoolean("update.ingameNotification", true);
    }

    private String loadUpdateStrategy() {
        return config.getString("update.strategy", "MINOR").toUpperCase(Locale.ROOT);
    }

    private boolean loadAutomatic() {
        return config.getBoolean("update.automatic", false);
    }
}
