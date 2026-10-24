package org.betonquest.betonquest.lib.config.section.multi.fallback;

import org.betonquest.betonquest.lib.config.section.multi.MultiSectionConfigurationTest;
import org.betonquest.betonquest.lib.config.util.ConfigurationBaseTest;
import org.junit.jupiter.api.Tag;

/**
 * This is a test for the {@link MultiFallbackConfiguration}.
 */
@Tag("ConfigurationSection")
@SuppressWarnings("PMD.JUnitJupiterTestShouldBePackagePrivate")
public class MultiSectionConfigurationWithMultiFallbackTest extends ConfigurationBaseTest {

    public MultiSectionConfigurationWithMultiFallbackTest() {
        super(new MultiFallbackConfiguration(MultiSectionConfigurationTest.getDefaultConfig(), null));
    }
}
