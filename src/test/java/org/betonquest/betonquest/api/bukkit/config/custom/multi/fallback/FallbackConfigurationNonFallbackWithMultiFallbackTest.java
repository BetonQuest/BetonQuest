package org.betonquest.betonquest.api.bukkit.config.custom.multi.fallback;

import org.betonquest.betonquest.api.bukkit.config.custom.fallback.FallbackConfiguration;
import org.betonquest.betonquest.api.bukkit.config.custom.fallback.FallbackConfigurationNonFallbackTest;
import org.bukkit.configuration.Configuration;
import org.junit.jupiter.api.Tag;

/**
 * Tests the {@link FallbackConfiguration} class.
 */
@Tag("ConfigurationSection")
@SuppressWarnings("PMD.JUnitAssertionsShouldIncludeMessage")
class FallbackConfigurationNonFallbackWithMultiFallbackTest extends FallbackConfigurationNonFallbackTest {
    @Override
    public Configuration getConfig() {
        return new FallbackConfiguration(getDefaultConfig(), null);
    }
}
