package org.betonquest.betonquest.api.bukkit.config.custom.multi.fallback;

import org.betonquest.betonquest.api.bukkit.config.custom.fallback.FallbackConfiguration;
import org.betonquest.betonquest.api.bukkit.config.custom.fallback.FallbackConfigurationEmptyOriginalTest;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.MemoryConfiguration;
import org.junit.jupiter.api.Tag;

/**
 * This is a test for the {@link MultiFallbackConfiguration}.
 */
@Tag("ConfigurationSection")
@SuppressWarnings({"PMD.JUnitAssertionsShouldIncludeMessage", "PMD.JUnit5TestShouldBePackagePrivate"})
public class FallbackConfigurationEmptyOriginalWithMultiFallbackTest extends FallbackConfigurationEmptyOriginalTest {
    @Override
    public Configuration getConfig() {
        fallback = getDefaultConfig();
        return new FallbackConfiguration(new MemoryConfiguration(), fallback);
    }
}
