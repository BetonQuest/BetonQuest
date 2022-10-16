package org.betonquest.betonquest.api.bukkit.config.custom.multi.fallback;

import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiConfiguration;
import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiConfigurationModificationTest;
import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiConfigurationSectionConfiguration;
import org.bukkit.configuration.Configuration;

/**
 * This is a test for modifications of the {@link MultiConfiguration}.
 */
@SuppressWarnings({"PMD.JUnitAssertionsShouldIncludeMessage", "PMD.JUnitTestContainsTooManyAsserts"})
class MultiConfigurationModificationWithMultiFallbackTest<T extends Configuration & MultiConfigurationSectionConfiguration> extends MultiConfigurationModificationTest {

    @SuppressWarnings("unchecked")
    @Override
    protected T getConfig() {
        return (T) new MultiFallbackConfiguration((T) super.getConfig(), null);
    }
}
