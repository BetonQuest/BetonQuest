package org.betonquest.betonquest.lib.config.section.multi.fallback;

import org.betonquest.betonquest.lib.config.section.fallback.AbstractFallbackConfigurationSectionTest;
import org.betonquest.betonquest.lib.config.section.multi.InvalidSubConfigurationException;
import org.betonquest.betonquest.lib.config.section.multi.KeyConflictException;
import org.betonquest.betonquest.lib.config.section.multi.MultiSectionConfiguration;
import org.betonquest.betonquest.lib.config.util.ConfigurationSectionBaseTest;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
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
public class FallbackConfigurationSectionNestedWithMultiFallbackTest extends AbstractFallbackConfigurationSectionTest {

    public FallbackConfigurationSectionNestedWithMultiFallbackTest() {
        super(createNestedSection(setupFallback()), setupFallback());
    }

    private static ConfigurationSection createNestedSection(final Configuration fallback) {
        final Configuration original = setupOriginal();

        final Configuration defaults = ConfigurationSectionBaseTest.getDefaultConfig().getDefaults();
        assertNotNull(defaults);
        original.setDefaults(defaults);

        final Configuration originalRoot = new MemoryConfiguration();
        final Configuration fallbackRoot = new MemoryConfiguration();
        originalRoot.set("original.nested.section", original);
        fallbackRoot.set("fallback.nested.section", fallback);
        final ConfigurationSection originalSection = originalRoot.getConfigurationSection("original.nested.section");
        final ConfigurationSection fallbackSection = fallbackRoot.getConfigurationSection("fallback.nested.section");
        assertNotNull(originalSection);
        assertNotNull(fallbackSection);

        final Map<ConfigurationSection, String> configs = new HashMap<>();
        configs.put(originalSection, "config.yml");
        try {
            return new MultiFallbackConfiguration(new MultiSectionConfiguration(List.of(originalSection)), fallbackSection);
        } catch (final KeyConflictException e) {
            fail(e.resolvedMessage(configs), e);
        } catch (final InvalidSubConfigurationException e) {
            fail(e);
        }
        return null;
    }
}
