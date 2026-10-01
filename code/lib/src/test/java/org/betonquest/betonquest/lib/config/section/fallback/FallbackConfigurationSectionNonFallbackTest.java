package org.betonquest.betonquest.lib.config.section.fallback;

import org.betonquest.betonquest.lib.config.util.ConfigurationSectionBaseTest;
import org.junit.jupiter.api.Tag;

/**
 * Tests the {@link FallbackConfigurationSection} class.
 */
@Tag("ConfigurationSection")
@SuppressWarnings("PMD.JUnitJupiterTestShouldBePackagePrivate")
public class FallbackConfigurationSectionNonFallbackTest extends ConfigurationSectionBaseTest {

    public FallbackConfigurationSectionNonFallbackTest() {
        super(new FallbackConfiguration(getDefaultConfig(), null));
    }
}
