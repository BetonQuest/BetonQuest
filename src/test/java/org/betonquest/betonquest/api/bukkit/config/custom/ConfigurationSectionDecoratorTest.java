package org.betonquest.betonquest.api.bukkit.config.custom;

import org.betonquest.betonquest.api.bukkit.config.util.AbstractConfigurationSectionTest;
import org.bukkit.configuration.ConfigurationSection;

/**
 * This is a test for {@link ConfigurationSectionDecoratorTest}.
 */
public class ConfigurationSectionDecoratorTest extends AbstractConfigurationSectionTest {

    /**
     * Empty constructor
     */
    public ConfigurationSectionDecoratorTest() {
        super();
    }

    @Override
    public ConfigurationSection getConfig() {
        return new ConfigurationSectionDecorator(super.getDefaultConfig());
    }
}
