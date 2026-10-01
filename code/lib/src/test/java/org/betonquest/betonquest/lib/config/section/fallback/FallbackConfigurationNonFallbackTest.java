package org.betonquest.betonquest.lib.config.section.fallback;

import org.betonquest.betonquest.lib.config.util.ConfigurationBaseTest;
import org.junit.jupiter.api.Tag;

/**
 * Tests the {@link FallbackConfiguration} class.
 */
@Tag("ConfigurationSection")
@SuppressWarnings("PMD.JUnitJupiterTestShouldBePackagePrivate")
public class FallbackConfigurationNonFallbackTest extends ConfigurationBaseTest {

    public FallbackConfigurationNonFallbackTest() {
        super(new FallbackConfiguration(getDefaultConfig(), null));
    }
}
