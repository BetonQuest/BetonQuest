package org.betonquest.betonquest.api.bukkit.config.custom.fallback;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.MemoryConfiguration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class tests edge cases of the {@link FallbackConfigurationSection}.
 */
@Tag("ConfigurationSection")
@SuppressWarnings("PMD.JUnit5TestShouldBePackagePrivate")
public class FallbackConfigurationSectionEdgeCaseTest {
    @Test
    void testInvalidConstructor() {
        final Configuration original = new MemoryConfiguration();
        assertThrows(IllegalStateException.class, () -> new FallbackConfigurationSection(original, null));
    }
}
