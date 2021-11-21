package org.betonquest.betonquest.api.bukkit.config.custom;

import org.betonquest.betonquest.api.bukkit.config.util.AbstractConfigurationSectionTest;
import org.bukkit.configuration.ConfigurationSection;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

@Execution(ExecutionMode.CONCURRENT)
public class ConfigurationSectionDecoratorTest extends AbstractConfigurationSectionTest {
    @Override
    public ConfigurationSection getConfig() {
        return new ConfigurationSectionDecorator(super.getConfig());
    }
}
