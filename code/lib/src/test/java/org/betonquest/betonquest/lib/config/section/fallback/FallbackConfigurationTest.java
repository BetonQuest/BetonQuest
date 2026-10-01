package org.betonquest.betonquest.lib.config.section.fallback;

import org.junit.jupiter.api.Tag;

/**
 * Tests the {@link FallbackConfiguration} class.
 */
@Tag("ConfigurationSection")
@SuppressWarnings({"PMD.UnitTestAssertionsShouldIncludeMessage", "PMD.JUnitJupiterTestShouldBePackagePrivate"})
public class FallbackConfigurationTest extends AbstractFallbackConfigurationTest {

    public FallbackConfigurationTest() {
        super(getDefaultConfig(setupFallback()), setupFallback());
    }
}
