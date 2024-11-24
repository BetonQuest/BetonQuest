package org.betonquest.betonquest.api.bukkit.config.custom.fallback;

import org.betonquest.betonquest.api.bukkit.config.util.ConfigurationSectionBaseTest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.junit.jupiter.api.Tag;

/**
 * Tests the {@link FallbackConfigurationSection} class.
 */
@Tag("ConfigurationSection")
@SuppressWarnings({"PMD.UnitTestAssertionsShouldIncludeMessage", "PMD.TestClassWithoutTestCases"})
public class FallbackConfigurationSectionNonFallbackTest extends ConfigurationSectionBaseTest {
    @Override
    public ConfigurationSection getConfig() throws InvalidConfigurationException {
        return new FallbackConfiguration(getDefaultConfig(), null);
    }
}
