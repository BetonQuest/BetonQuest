package org.betonquest.betonquest.api.bukkit.config.custom.multi.fallback;

import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiConfiguration;
import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiSectionConfigurationModificationTest;
import org.junit.jupiter.api.Tag;

/**
 * This is a test for the {@link MultiFallbackConfiguration}.
 */
@Tag("ConfigurationSection")
@SuppressWarnings({"PMD.UnitTestAssertionsShouldIncludeMessage", "PMD.UnitTestContainsTooManyAsserts",
        "PMD.TestClassWithoutTestCases"})
class MultiSectionConfigurationModificationWithMultiFallbackTest extends MultiSectionConfigurationModificationTest {

    @Override
    protected MultiConfiguration getConfig() {
        return new MultiFallbackConfiguration(super.getConfig(), null);
    }
}
