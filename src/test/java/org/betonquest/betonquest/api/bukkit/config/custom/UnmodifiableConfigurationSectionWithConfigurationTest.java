package org.betonquest.betonquest.api.bukkit.config.custom;

import org.bukkit.configuration.ConfigurationSection;

/**
 * This is a test for {@link UnmodifiableConfiguration} as a {@link ConfigurationSection}.
 */
public class UnmodifiableConfigurationSectionWithConfigurationTest extends UnmodifiableConfigurationSectionTest {

    /**
     * Empty constructor
     */
    public UnmodifiableConfigurationSectionWithConfigurationTest() {
        super();
    }

    @Override
    public ConfigurationSection getConfig() {
        return new UnmodifiableConfiguration(getDefaultConfig());
    }
}
