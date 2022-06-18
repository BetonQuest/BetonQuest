package org.betonquest.betonquest.api.bukkit.config.custom;

import org.betonquest.betonquest.api.bukkit.config.util.ConfigurationSectionBaseTest;
import org.bukkit.configuration.ConfigurationSection;

/**
 * This is a test for the {@link ConfigurationSectionDecorator}.
 */
public class ConfigurationSectionDecoratorTest extends ConfigurationSectionBaseTest {

    @Override
    public ConfigurationSection getConfig() {
        return new ConfigurationSectionDecorator(super.getDefaultConfig());
    }
}
