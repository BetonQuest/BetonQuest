package org.betonquest.betonquest.lib.config.util;

import org.junit.jupiter.api.Tag;

/**
 * Tests the default {@link org.bukkit.configuration.Configuration} implementation.
 */
@Tag("ConfigurationSection")
@SuppressWarnings("PMD.JUnitJupiterTestShouldBePackagePrivate")
public class DefaultConfigurationTest extends ConfigurationBaseTest {

    public DefaultConfigurationTest() {
        super(getDefaultConfig());
    }
}
