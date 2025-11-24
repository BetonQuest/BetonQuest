package org.betonquest.betonquest.api.bukkit.config.custom.fallback;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.MemoryConfiguration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the {@link FallbackConfigurationSection} class.
 */
@Tag("ConfigurationSection")
@SuppressWarnings({"PMD.UnitTestAssertionsShouldIncludeMessage", "PMD.JUnit5TestShouldBePackagePrivate"})
public class FallbackConfigurationEmptyOriginalTest extends FallbackConfigurationTest {
    @Override
    public Configuration getConfig() throws InvalidConfigurationException {
        fallback = getDefaultConfig();
        return new FallbackConfiguration(new MemoryConfiguration(), fallback);
    }

    @Test
    @Override
    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    public void testSetDefaults() {
        final Configuration defaultSection = new MemoryConfiguration();
        defaultSection.set("default.one", 1);
        defaultSection.set("default.two", 2);
        config.setDefaults(defaultSection);
        assertEquals(1, config.getInt("default.one"));
        assertEquals(2, config.getInt("default.two"));
        assertEquals("value", config.get("default.key"));
    }
}
