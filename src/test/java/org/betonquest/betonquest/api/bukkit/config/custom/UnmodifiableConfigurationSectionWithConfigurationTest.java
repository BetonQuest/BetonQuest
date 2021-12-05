package org.betonquest.betonquest.api.bukkit.config.custom;

import org.bukkit.configuration.ConfigurationSection;

public class UnmodifiableConfigurationSectionWithConfigurationTest extends UnmodifiableConfigurationSectionTest{
    @Override
    public ConfigurationSection getConfig() {
        return new UnmodifiableConfiguration(getDefaultConfig());
    }
}
