package org.betonquest.betonquest.lib.config.section.multi.fallback;

import org.betonquest.betonquest.lib.config.section.fallback.AbstractFallbackConfigurationTest;
import org.betonquest.betonquest.lib.config.section.multi.InvalidSubConfigurationException;
import org.betonquest.betonquest.lib.config.section.multi.KeyConflictException;
import org.betonquest.betonquest.lib.config.section.multi.MultiSectionConfiguration;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.junit.jupiter.api.Tag;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is a test for the {@link MultiFallbackConfiguration}.
 */
@Tag("ConfigurationSection")
@SuppressWarnings({"PMD.UnitTestAssertionsShouldIncludeMessage", "PMD.JUnitJupiterTestShouldBePackagePrivate"})
public class FallbackConfigurationWithMultiFallbackTest extends AbstractFallbackConfigurationTest {

    public FallbackConfigurationWithMultiFallbackTest() {
        super(getDefaultConfig(setupFallback()), setupFallback());
    }

    public static Configuration getDefaultConfig(final Configuration fallback) {
        final Configuration original = setupOriginal();

        final Configuration defaults = AbstractFallbackConfigurationTest.getDefaultConfig(fallback).getDefaults();
        if (defaults != null) {
            original.setDefaults(defaults);
        }

        final Map<ConfigurationSection, String> configs = new HashMap<>();
        configs.put(original, "config.yml");
        try {
            return new MultiFallbackConfiguration(new MultiSectionConfiguration(List.of(original)), fallback);
        } catch (final KeyConflictException e) {
            fail(e.resolvedMessage(configs), e);
        } catch (final InvalidSubConfigurationException e) {
            fail(e);
        }
        return null;
    }
}
