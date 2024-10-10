package org.betonquest.betonquest.api.bukkit.config.custom.multi.fallback;

import org.betonquest.betonquest.api.bukkit.config.custom.fallback.FallbackConfigurationSectionEmptyOriginalTest;
import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiSectionConfiguration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.MemoryConfiguration;
import org.junit.jupiter.api.Tag;

import java.util.List;

/**
 * This is a test for the {@link MultiFallbackConfiguration}.
 */
@Tag("ConfigurationSection")
@SuppressWarnings({"PMD.JUnitAssertionsShouldIncludeMessage", "PMD.JUnit5TestShouldBePackagePrivate",
        "PMD.TestClassWithoutTestCases"})
public class FallbackConfigurationSectionEmptyOriginalWithMultiFallbackTest extends FallbackConfigurationSectionEmptyOriginalTest {
    @Override
    public ConfigurationSection getConfig() throws InvalidConfigurationException {
        fallback = getDefaultConfig();
        original = new MultiSectionConfiguration(List.of(new MemoryConfiguration()));
        return new MultiFallbackConfiguration((MultiSectionConfiguration) original, fallback);
    }
}
