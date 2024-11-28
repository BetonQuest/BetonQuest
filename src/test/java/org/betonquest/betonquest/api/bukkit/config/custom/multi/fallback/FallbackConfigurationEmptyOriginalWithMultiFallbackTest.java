package org.betonquest.betonquest.api.bukkit.config.custom.multi.fallback;

import org.betonquest.betonquest.api.bukkit.config.custom.fallback.FallbackConfigurationEmptyOriginalTest;
import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiSectionConfiguration;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.MemoryConfiguration;
import org.junit.jupiter.api.Tag;

import java.util.List;

/**
 * This is a test for the {@link MultiFallbackConfiguration}.
 */
@Tag("ConfigurationSection")
@SuppressWarnings({"PMD.UnitTestAssertionsShouldIncludeMessage", "PMD.JUnit5TestShouldBePackagePrivate",
        "PMD.TestClassWithoutTestCases"})
public class FallbackConfigurationEmptyOriginalWithMultiFallbackTest extends FallbackConfigurationEmptyOriginalTest {
    @Override
    public Configuration getConfig() throws InvalidConfigurationException {
        fallback = getDefaultConfig();
        return new MultiFallbackConfiguration(new MultiSectionConfiguration(List.of(new MemoryConfiguration())), fallback);
    }
}
