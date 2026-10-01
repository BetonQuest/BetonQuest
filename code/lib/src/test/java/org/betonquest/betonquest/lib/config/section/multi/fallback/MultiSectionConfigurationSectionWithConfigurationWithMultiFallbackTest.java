package org.betonquest.betonquest.lib.config.section.multi.fallback;

import org.betonquest.betonquest.lib.config.section.multi.MultiSectionConfigurationSectionWithConfigurationTest;
import org.betonquest.betonquest.lib.config.section.multi.MultiSectionConfigurationTest;
import org.bukkit.configuration.ConfigurationSection;
import org.junit.jupiter.api.Tag;

/**
 * This is a test for {@link MultiFallbackConfiguration} as a {@link ConfigurationSection}.
 */
@Tag("ConfigurationSection")
@SuppressWarnings("PMD.JUnitJupiterTestShouldBePackagePrivate")
public class MultiSectionConfigurationSectionWithConfigurationWithMultiFallbackTest extends MultiSectionConfigurationSectionWithConfigurationTest {

    public MultiSectionConfigurationSectionWithConfigurationWithMultiFallbackTest() {
        super(new MultiFallbackConfiguration(MultiSectionConfigurationTest.getDefaultConfig(), null));
    }
}
