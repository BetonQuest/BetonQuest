package org.betonquest.betonquest.lib.config.section.fallback;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.MemoryConfiguration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Base abstract test class for {@link FallbackConfiguration} with empty original.
 */
@Tag("ConfigurationSection")
@SuppressWarnings({"PMD.UnitTestAssertionsShouldIncludeMessage", "PMD.JUnitJupiterTestShouldBePackagePrivate"})
public abstract class AbstractFallbackConfigurationEmptyOriginalTest extends AbstractFallbackConfigurationTest {

    public AbstractFallbackConfigurationEmptyOriginalTest(final Configuration config, final Configuration fallback) {
        super(config, fallback);
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
