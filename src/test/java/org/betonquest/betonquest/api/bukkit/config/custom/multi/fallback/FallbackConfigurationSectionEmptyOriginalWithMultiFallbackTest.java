package org.betonquest.betonquest.api.bukkit.config.custom.multi.fallback;

import org.betonquest.betonquest.api.bukkit.config.custom.fallback.FallbackConfiguration;
import org.betonquest.betonquest.api.bukkit.config.custom.fallback.FallbackConfigurationSectionEmptyOriginalTest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.junit.jupiter.api.Tag;

/**
 * This is a test for the {@link MultiFallbackConfiguration}.
 */
@Tag("ConfigurationSection")
@SuppressWarnings({"PMD.JUnitAssertionsShouldIncludeMessage", "PMD.JUnit5TestShouldBePackagePrivate",
        "PMD.TestClassWithoutTestCases"})
public class FallbackConfigurationSectionEmptyOriginalWithMultiFallbackTest extends FallbackConfigurationSectionEmptyOriginalTest {
    @Override
    public ConfigurationSection getConfig() {
        fallback = getDefaultConfig();
        return new FallbackConfiguration(new MemoryConfiguration(), fallback);
    }
}
