package org.betonquest.betonquest.lib.config.section;

import org.betonquest.betonquest.lib.config.util.ConfigurationSectionBaseTest;
import org.bukkit.configuration.ConfigurationSection;
import org.junit.jupiter.api.Tag;

/**
 * This is a test for the {@link ConfigurationSectionDecorator}.
 */
@Tag("ConfigurationSection")
@SuppressWarnings("PMD.TestClassWithoutTestCases")
public class ConfigurationSectionDecoratorTest extends ConfigurationSectionBaseTest {

    @Override
    public ConfigurationSection getConfig() {
        return new ConfigurationSectionDecorator(super.getDefaultConfig());
    }
}
