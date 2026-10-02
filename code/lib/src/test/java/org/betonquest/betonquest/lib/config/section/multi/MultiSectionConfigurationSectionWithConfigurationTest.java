package org.betonquest.betonquest.lib.config.section.multi;

import org.betonquest.betonquest.lib.config.util.ConfigurationSectionBaseTest;
import org.bukkit.configuration.ConfigurationSection;
import org.junit.jupiter.api.Tag;

/**
 * This is a test for {@link MultiSectionConfiguration} as a {@link ConfigurationSection}.
 */
@Tag("ConfigurationSection")
@SuppressWarnings("PMD.JUnitJupiterTestShouldBePackagePrivate")
public class MultiSectionConfigurationSectionWithConfigurationTest extends ConfigurationSectionBaseTest {

    public MultiSectionConfigurationSectionWithConfigurationTest() {
        super(MultiSectionConfigurationTest.getDefaultConfig());
    }
}
