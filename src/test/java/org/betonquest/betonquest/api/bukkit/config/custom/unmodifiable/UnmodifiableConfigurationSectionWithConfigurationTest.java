package org.betonquest.betonquest.api.bukkit.config.custom.unmodifiable;

import org.bukkit.configuration.ConfigurationSection;

/**
 * This is a test for {@link UnmodifiableConfiguration} as a {@link ConfigurationSection}.
 */
public class UnmodifiableConfigurationSectionWithConfigurationTest extends UnmodifiableConfigurationSectionTest {

    @Override
    public ConfigurationSection getConfig() {
        return new UnmodifiableConfiguration(getDefaultConfig());
    }
}
