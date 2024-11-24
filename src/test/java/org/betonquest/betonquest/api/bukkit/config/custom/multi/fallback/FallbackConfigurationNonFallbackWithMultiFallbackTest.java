package org.betonquest.betonquest.api.bukkit.config.custom.multi.fallback;

import org.betonquest.betonquest.api.bukkit.config.custom.fallback.FallbackConfigurationNonFallbackTest;
import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiSectionConfiguration;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.InvalidConfigurationException;
import org.junit.jupiter.api.Tag;

import java.util.List;

/**
 * This is a test for the {@link MultiFallbackConfiguration}.
 */
@Tag("ConfigurationSection")
@SuppressWarnings({"PMD.UnitTestAssertionsShouldIncludeMessage", "PMD.TestClassWithoutTestCases"})
class FallbackConfigurationNonFallbackWithMultiFallbackTest extends FallbackConfigurationNonFallbackTest {
    @Override
    public Configuration getConfig() throws InvalidConfigurationException {
        return new MultiFallbackConfiguration(new MultiSectionConfiguration(List.of(getDefaultConfig())), null);
    }
}
