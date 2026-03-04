package org.betonquest.betonquest.lib.config.section.multi.fallback;

import org.betonquest.betonquest.api.config.section.multi.MultiConfiguration;
import org.betonquest.betonquest.lib.config.section.multi.MultiSectionConfigurationModificationTest;
import org.junit.jupiter.api.Tag;

/**
 * This is a test for the {@link MultiFallbackConfiguration}.
 */
@Tag("ConfigurationSection")
@SuppressWarnings("PMD.TestClassWithoutTestCases")
class MultiSectionConfigurationModificationWithMultiFallbackTest extends MultiSectionConfigurationModificationTest {

    @Override
    protected MultiConfiguration getConfig() {
        return new MultiFallbackConfiguration(super.getConfig(), null);
    }
}
