package org.betonquest.betonquest.lib.config.section.multi.fallback;

import org.betonquest.betonquest.lib.config.section.multi.MultiSectionConfigurationSplitTest;
import org.betonquest.betonquest.lib.config.util.ConfigurationSectionBaseTest;
import org.bukkit.configuration.ConfigurationSection;
import org.junit.jupiter.api.Tag;

/**
 * This is a test for {@link MultiFallbackConfiguration} as a {@link ConfigurationSection}.
 */
@Tag("ConfigurationSection")
@SuppressWarnings("PMD.JUnitJupiterTestShouldBePackagePrivate")
public class MultiSectionConfigurationSectionSplitWithConfigurationWithMultiFallbackTest extends ConfigurationSectionBaseTest {

    public MultiSectionConfigurationSectionSplitWithConfigurationWithMultiFallbackTest() {
        super(new MultiFallbackConfiguration(MultiSectionConfigurationSplitTest.getDefaultConfig(), null));
    }
}
