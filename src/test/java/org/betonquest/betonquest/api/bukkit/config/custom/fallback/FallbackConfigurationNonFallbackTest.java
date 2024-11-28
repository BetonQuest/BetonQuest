package org.betonquest.betonquest.api.bukkit.config.custom.fallback;

import org.betonquest.betonquest.api.bukkit.config.util.ConfigurationBaseTest;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.InvalidConfigurationException;
import org.junit.jupiter.api.Tag;

/**
 * Tests the {@link FallbackConfiguration} class.
 */
@Tag("ConfigurationSection")
@SuppressWarnings({"PMD.UnitTestAssertionsShouldIncludeMessage", "PMD.TestClassWithoutTestCases"})
public class FallbackConfigurationNonFallbackTest extends ConfigurationBaseTest {
    @Override
    public Configuration getConfig() throws InvalidConfigurationException {
        return new FallbackConfiguration(getDefaultConfig(), null);
    }
}
