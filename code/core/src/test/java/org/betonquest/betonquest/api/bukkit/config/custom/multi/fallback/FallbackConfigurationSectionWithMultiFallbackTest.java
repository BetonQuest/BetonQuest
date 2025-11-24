package org.betonquest.betonquest.api.bukkit.config.custom.multi.fallback;

import org.betonquest.betonquest.api.bukkit.config.custom.fallback.FallbackConfigurationSectionTest;
import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiSectionConfiguration;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.junit.jupiter.api.Tag;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is a test for the {@link MultiFallbackConfiguration}.
 */
@Tag("ConfigurationSection")
@SuppressWarnings({"PMD.UnitTestAssertionsShouldIncludeMessage", "PMD.TestClassWithoutTestCases"})
public class FallbackConfigurationSectionWithMultiFallbackTest extends FallbackConfigurationSectionTest {
    @Override
    public ConfigurationSection getConfig() throws InvalidConfigurationException {
        final Configuration original = setupOriginal();
        fallback = setupFallback();

        final Configuration defaults = super.getDefaultConfig().getDefaults();
        assertNotNull(defaults);
        original.setDefaults(defaults);

        return new MultiFallbackConfiguration(new MultiSectionConfiguration(List.of(original)), fallback);
    }
}
