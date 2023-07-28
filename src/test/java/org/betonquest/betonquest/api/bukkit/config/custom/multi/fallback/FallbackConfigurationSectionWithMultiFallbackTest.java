package org.betonquest.betonquest.api.bukkit.config.custom.multi.fallback;

import org.betonquest.betonquest.api.bukkit.config.custom.fallback.FallbackConfiguration;
import org.betonquest.betonquest.api.bukkit.config.custom.fallback.FallbackConfigurationSectionTest;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.junit.jupiter.api.Tag;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is a test for the {@link MultiFallbackConfiguration}.
 */
@Tag("ConfigurationSection")
@SuppressWarnings({"PMD.JUnitAssertionsShouldIncludeMessage", "PMD.TestClassWithoutTestCases"})
public class FallbackConfigurationSectionWithMultiFallbackTest extends FallbackConfigurationSectionTest {
    @Override
    public ConfigurationSection getConfig() {
        final Configuration original = setupOriginal();
        fallback = setupFallback();

        final Configuration defaults = super.getDefaultConfig().getDefaults();
        assertNotNull(defaults);
        original.setDefaults(defaults);

        return new FallbackConfiguration(original, fallback);
    }
}
