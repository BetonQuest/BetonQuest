package org.betonquest.betonquest.api.bukkit.config.custom.multi;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;

import java.io.Serial;

/**
 * This exception represents an invalid sub configuration in a {@link MultiConfiguration}.
 */
public class InvalidSubConfigurationException extends InvalidConfigurationException {

    @Serial
    private static final long serialVersionUID = 8273686920162391985L;

    /**
     * The invalid sub configuration.
     */
    private final ConfigurationSection subConfiguration;

    /**
     * Creates an exception for an invalid sub configuration.
     *
     * @param msg              the exception message
     * @param subConfiguration the related sub configuration
     */
    public InvalidSubConfigurationException(final String msg, final ConfigurationSection subConfiguration) {
        super(msg);
        this.subConfiguration = subConfiguration;
    }

    /**
     * Gets the invalid sub configuration.
     *
     * @return the invalid sub configuration
     */
    public ConfigurationSection getSubConfiguration() {
        return subConfiguration;
    }
}
