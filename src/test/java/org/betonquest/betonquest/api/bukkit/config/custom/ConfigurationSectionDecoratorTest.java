package org.betonquest.betonquest.api.bukkit.config.custom;

import org.betonquest.betonquest.api.bukkit.config.util.AbstractConfigurationSectionTest;
import org.bukkit.configuration.ConfigurationSection;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

/**
 * This is a test for {@link ConfigurationSectionDecoratorTest}.
 */
@Execution(ExecutionMode.CONCURRENT)
public class ConfigurationSectionDecoratorTest extends AbstractConfigurationSectionTest {

    /**
     * Empty constructor
     */
    public ConfigurationSectionDecoratorTest() {
        super();
    }

    @Override
    public ConfigurationSection getConfig() {
        return new ConfigurationSectionDecorator(super.getConfig());
    }
}
