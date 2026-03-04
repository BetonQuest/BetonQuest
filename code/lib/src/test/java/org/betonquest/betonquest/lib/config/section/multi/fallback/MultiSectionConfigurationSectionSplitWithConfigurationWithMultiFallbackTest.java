package org.betonquest.betonquest.lib.config.section.multi.fallback;

import org.betonquest.betonquest.api.config.section.multi.MultiConfiguration;
import org.betonquest.betonquest.lib.config.section.multi.MultiSectionConfigurationSectionSplitWithConfigurationTest;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.junit.jupiter.api.Tag;

/**
 * This is a test for {@link MultiFallbackConfiguration} as a {@link ConfigurationSection}.
 */
@Tag("ConfigurationSection")
@SuppressWarnings("PMD.TestClassWithoutTestCases")
public class MultiSectionConfigurationSectionSplitWithConfigurationWithMultiFallbackTest extends MultiSectionConfigurationSectionSplitWithConfigurationTest {

    @Override
    public Configuration getConfig() {
        return new MultiFallbackConfiguration((MultiConfiguration) super.getConfig(), null);
    }
}
