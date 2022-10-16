package org.betonquest.betonquest.api.bukkit.config.custom.multi.fallback;

import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiConfiguration;
import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiConfigurationTest;
import org.bukkit.configuration.Configuration;

/**
 * This is a test for the {@link MultiConfiguration}.
 */
@SuppressWarnings({"PMD.JUnit5TestShouldBePackagePrivate", "PMD.JUnitAssertionsShouldIncludeMessage"})
public class MultiConfigurationWithMultiFallbackTest extends MultiConfigurationTest {

    @Override
    public Configuration getConfig() {
        return new MultiFallbackConfiguration((MultiConfiguration) super.getConfig(), null);
    }
}
