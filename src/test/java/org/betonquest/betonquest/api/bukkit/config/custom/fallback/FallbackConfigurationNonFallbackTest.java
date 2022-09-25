package org.betonquest.betonquest.api.bukkit.config.custom.fallback;

import org.betonquest.betonquest.api.bukkit.config.util.ConfigurationBaseTest;
import org.bukkit.configuration.Configuration;

/**
 * Tests the {@link FallbackConfiguration} class.
 */
@SuppressWarnings("PMD.JUnitAssertionsShouldIncludeMessage")
class FallbackConfigurationNonFallbackTest extends ConfigurationBaseTest {
    @Override
    public Configuration getConfig() {
        return new FallbackConfiguration(getDefaultConfig(), null);
    }
}
