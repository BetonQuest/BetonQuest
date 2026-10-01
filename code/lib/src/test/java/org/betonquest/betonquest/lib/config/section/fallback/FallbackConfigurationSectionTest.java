package org.betonquest.betonquest.lib.config.section.fallback;

import org.junit.jupiter.api.Tag;

/**
 * Tests the {@link FallbackConfigurationSection} class.
 */
@Tag("ConfigurationSection")
@SuppressWarnings("PMD.JUnitJupiterTestShouldBePackagePrivate")
public class FallbackConfigurationSectionTest extends AbstractFallbackConfigurationSectionTest {

    public FallbackConfigurationSectionTest() {
        super(getDefaultConfig(setupFallback()), setupFallback());
    }
}
