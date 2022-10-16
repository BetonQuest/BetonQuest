package org.betonquest.betonquest.api.bukkit.config.custom.multi.fallback;

import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiConfiguration;
import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiConfigurationSplitTest;
import org.bukkit.configuration.Configuration;

/**
 * This is a test for the {@link MultiConfiguration}.
 */
@SuppressWarnings({"PMD.JUnit5TestShouldBePackagePrivate", "PMD.JUnitAssertionsShouldIncludeMessage"})
public class MultiConfigurationSplitWithMultiFallbackTest extends MultiConfigurationSplitTest {

    @Override
    public Configuration getConfig() {
        return new MultiFallbackConfiguration((MultiConfiguration) super.getConfig(), null);
    }
}
