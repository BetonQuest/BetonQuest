package org.betonquest.betonquest.api.bukkit.config.custom.multi.fallback;

import org.betonquest.betonquest.api.bukkit.config.custom.fallback.FallbackConfigurationSectionNonFallbackTest;
import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiSectionConfiguration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.junit.jupiter.api.Tag;

import java.util.List;

/**
 * This is a test for the {@link MultiFallbackConfiguration}.
 */
@Tag("ConfigurationSection")
@SuppressWarnings({"PMD.UnitTestAssertionsShouldIncludeMessage", "PMD.TestClassWithoutTestCases"})
public class FallbackConfigurationSectionNonFallbackWithMultiFallbackTest extends FallbackConfigurationSectionNonFallbackTest {
    @Override
    public ConfigurationSection getConfig() throws InvalidConfigurationException {
        return new MultiFallbackConfiguration(new MultiSectionConfiguration(List.of(getDefaultConfig())), null);
    }
}
