package org.betonquest.betonquest.lib.config.section.multi.fallback;

import org.betonquest.betonquest.lib.config.section.multi.MultiSectionConfigurationSplitTest;
import org.betonquest.betonquest.lib.config.util.ConfigurationBaseTest;
import org.junit.jupiter.api.Tag;

/**
 * This is a test for the {@link MultiFallbackConfiguration}.
 */
@Tag("ConfigurationSection")
@SuppressWarnings("PMD.JUnitJupiterTestShouldBePackagePrivate")
public class MultiSectionConfigurationSplitWithMultiFallbackTest extends ConfigurationBaseTest {

    public MultiSectionConfigurationSplitWithMultiFallbackTest() {
        super(new MultiFallbackConfiguration(MultiSectionConfigurationSplitTest.getDefaultConfig(), null));
    }
}
