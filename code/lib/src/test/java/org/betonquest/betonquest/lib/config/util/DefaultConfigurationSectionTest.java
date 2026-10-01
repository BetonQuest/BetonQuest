package org.betonquest.betonquest.lib.config.util;

import org.junit.jupiter.api.Tag;

/**
 * Tests the default {@link org.bukkit.configuration.ConfigurationSection} implementation.
 */
@Tag("ConfigurationSection")
@SuppressWarnings("PMD.JUnitJupiterTestShouldBePackagePrivate")
public class DefaultConfigurationSectionTest extends ConfigurationSectionBaseTest {

    public DefaultConfigurationSectionTest() {
        super(getDefaultConfig());
    }
}
