package org.betonquest.betonquest.api.bukkit.config.custom.fallback;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.MemoryConfiguration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class tests edge cases of the {@link FallbackConfiguration}.
 */
@Tag("ConfigurationSection")
@SuppressWarnings("PMD.UnitTestAssertionsShouldIncludeMessage")
class FallbackConfigurationEdgeCaseTest {

    @Test
    void testDefaultInstance() {
        final Configuration original = new MemoryConfiguration();
        final FallbackConfiguration config = new FallbackConfiguration(original, null);
        assertNull(config.getDefaults());
        config.addDefault("test", "test");
        assertNotNull(config.getDefaults());
    }
}
