package org.betonquest.betonquest.lib.config.section.fallback;

import org.bukkit.configuration.MemoryConfiguration;
import org.junit.jupiter.api.Tag;

/**
 * Tests the {@link FallbackConfigurationSection} class.
 */
@Tag("ConfigurationSection")
@SuppressWarnings("PMD.JUnitJupiterTestShouldBePackagePrivate")
public class FallbackConfigurationSectionEmptyOriginalTest extends AbstractFallbackConfigurationSectionEmptyOriginalTest {

    public FallbackConfigurationSectionEmptyOriginalTest() {
        super(new FallbackConfiguration(new MemoryConfiguration(), getDefaultConfig()), getDefaultConfig());
    }
}
