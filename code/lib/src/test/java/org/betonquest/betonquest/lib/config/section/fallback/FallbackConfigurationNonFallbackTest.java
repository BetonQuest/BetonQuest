package org.betonquest.betonquest.lib.config.section.fallback;

import org.betonquest.betonquest.lib.config.util.ConfigurationBaseTest;
import org.bukkit.configuration.Configuration;
import org.junit.jupiter.api.Tag;

/**
 * Tests the {@link FallbackConfiguration} class.
 */
@Tag("ConfigurationSection")
@SuppressWarnings("PMD.JUnitJupiterTestShouldBePackagePrivate")
public class FallbackConfigurationNonFallbackTest extends ConfigurationBaseTest {

    public FallbackConfigurationNonFallbackTest() {
        this(new FallbackConfiguration(getDefaultConfig(), null));
    }

    public FallbackConfigurationNonFallbackTest(final Configuration config) {
        super(config);
    }
}
