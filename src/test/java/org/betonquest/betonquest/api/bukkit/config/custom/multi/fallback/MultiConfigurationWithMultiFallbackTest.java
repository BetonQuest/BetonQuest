package org.betonquest.betonquest.api.bukkit.config.custom.multi.fallback;

import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiConfiguration;
import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiConfigurationTest;
import org.bukkit.configuration.Configuration;
import org.junit.jupiter.api.Tag;

/**
 * This is a test for the {@link MultiConfiguration}.
 */
@Tag("ConfigurationSection")
@SuppressWarnings({"PMD.JUnit5TestShouldBePackagePrivate", "PMD.JUnitAssertionsShouldIncludeMessage"})
public class MultiConfigurationWithMultiFallbackTest extends MultiConfigurationTest {

    @Override
    public Configuration getConfig() {
        return new MultiFallbackConfiguration((MultiConfiguration) super.getConfig(), null);
    }
}
