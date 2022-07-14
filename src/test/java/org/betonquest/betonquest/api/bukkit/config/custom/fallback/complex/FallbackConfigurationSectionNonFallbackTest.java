package org.betonquest.betonquest.api.bukkit.config.custom.fallback.complex;

import org.betonquest.betonquest.api.bukkit.config.custom.fallback.FallbackConfiguration;
import org.betonquest.betonquest.api.bukkit.config.custom.fallback.FallbackConfigurationSection;
import org.betonquest.betonquest.api.bukkit.config.util.ConfigurationSectionBaseTest;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Tests the {@link FallbackConfigurationSection} class.
 */
@SuppressWarnings("PMD.JUnitAssertionsShouldIncludeMessage")
public class FallbackConfigurationSectionNonFallbackTest extends ConfigurationSectionBaseTest {
    @Override
    public ConfigurationSection getConfig() {
        return new FallbackConfiguration(getDefaultConfig(), null);
    }
}
