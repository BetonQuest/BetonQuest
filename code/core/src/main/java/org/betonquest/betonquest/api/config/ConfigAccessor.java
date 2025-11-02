package org.betonquest.betonquest.api.config;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Represents a configuration file that can be accessed.
 */
public interface ConfigAccessor extends ConfigurationSection {

    /**
     * Gets the {@link Configuration} that was loaded by this {@link ConfigAccessor}.
     *
     * @return the configuration.
     */
    Configuration getConfig();
}
