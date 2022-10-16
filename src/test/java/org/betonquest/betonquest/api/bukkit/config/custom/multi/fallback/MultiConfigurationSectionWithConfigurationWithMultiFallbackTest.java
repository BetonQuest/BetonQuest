package org.betonquest.betonquest.api.bukkit.config.custom.multi.fallback;

import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiConfiguration;
import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiConfigurationSectionWithConfigurationTest;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.junit.jupiter.api.Tag;

/**
 * This is a test for {@link MultiConfiguration} as a {@link ConfigurationSection}.
 */
@Tag("ConfigurationSection")
@SuppressWarnings({"PMD.JUnit5TestShouldBePackagePrivate", "PMD.JUnitAssertionsShouldIncludeMessage"})
public class MultiConfigurationSectionWithConfigurationWithMultiFallbackTest extends MultiConfigurationSectionWithConfigurationTest {

    @Override
    public Configuration getConfig() {
        return new MultiFallbackConfiguration((MultiConfiguration) super.getConfig(), null);
    }
}
