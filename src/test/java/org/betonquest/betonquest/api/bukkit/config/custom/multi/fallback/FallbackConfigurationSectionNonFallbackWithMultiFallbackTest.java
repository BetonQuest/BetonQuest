package org.betonquest.betonquest.api.bukkit.config.custom.multi.fallback;

import org.betonquest.betonquest.api.bukkit.config.custom.fallback.FallbackConfiguration;
import org.betonquest.betonquest.api.bukkit.config.custom.fallback.FallbackConfigurationSection;
import org.betonquest.betonquest.api.bukkit.config.custom.fallback.FallbackConfigurationSectionNonFallbackTest;
import org.bukkit.configuration.ConfigurationSection;
import org.junit.jupiter.api.Tag;

/**
 * Tests the {@link FallbackConfigurationSection} class.
 */
@Tag("ConfigurationSection")
@SuppressWarnings("PMD.JUnitAssertionsShouldIncludeMessage")
public class FallbackConfigurationSectionNonFallbackWithMultiFallbackTest extends FallbackConfigurationSectionNonFallbackTest {
    @Override
    public ConfigurationSection getConfig() {
        return new FallbackConfiguration(getDefaultConfig(), null);
    }
}
