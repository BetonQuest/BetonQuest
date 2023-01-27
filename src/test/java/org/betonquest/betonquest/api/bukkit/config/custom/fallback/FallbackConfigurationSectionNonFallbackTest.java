package org.betonquest.betonquest.api.bukkit.config.custom.fallback;

import org.betonquest.betonquest.api.bukkit.config.util.ConfigurationSectionBaseTest;
import org.bukkit.configuration.ConfigurationSection;
import org.junit.jupiter.api.Tag;

/**
 * Tests the {@link FallbackConfigurationSection} class.
 */
@Tag("ConfigurationSection")
@SuppressWarnings({"PMD.JUnitAssertionsShouldIncludeMessage", "PMD.TestClassWithoutTestCases"})
public class FallbackConfigurationSectionNonFallbackTest extends ConfigurationSectionBaseTest {
    @Override
    public ConfigurationSection getConfig() {
        return new FallbackConfiguration(getDefaultConfig(), null);
    }
}
