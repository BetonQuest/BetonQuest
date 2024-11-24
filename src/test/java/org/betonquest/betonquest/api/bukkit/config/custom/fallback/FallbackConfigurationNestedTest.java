package org.betonquest.betonquest.api.bukkit.config.custom.fallback;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.MemoryConfiguration;
import org.junit.jupiter.api.Tag;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the {@link FallbackConfiguration} class.
 */
@Tag("ConfigurationSection")
@SuppressWarnings({"PMD.UnitTestAssertionsShouldIncludeMessage", "PMD.TestClassWithoutTestCases"})
public class FallbackConfigurationNestedTest extends FallbackConfigurationTest {
    @Override
    public Configuration getConfig() throws InvalidConfigurationException {
        final Configuration original = setupOriginal();
        fallback = setupFallback();

        final Configuration defaults = super.getDefaultConfig().getDefaults();
        assertNotNull(defaults);
        original.setDefaults(defaults);

        final Configuration originalRoot = new MemoryConfiguration();
        final Configuration fallbackRoot = new MemoryConfiguration();
        originalRoot.set("original.nested.section", original);
        fallbackRoot.set("fallback.nested.section", fallback);
        final ConfigurationSection originalSection = originalRoot.getConfigurationSection("original.nested.section");
        final ConfigurationSection fallbackSection = fallbackRoot.getConfigurationSection("fallback.nested.section");
        assertNotNull(originalSection);
        assertNotNull(fallbackSection);

        return new FallbackConfiguration(originalSection, fallbackSection);
    }
}
