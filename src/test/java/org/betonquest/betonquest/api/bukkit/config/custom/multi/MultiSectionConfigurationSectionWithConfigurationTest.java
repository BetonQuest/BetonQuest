package org.betonquest.betonquest.api.bukkit.config.custom.multi;

import org.betonquest.betonquest.api.bukkit.config.util.ConfigurationSectionBaseTest;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is a test for {@link MultiSectionConfiguration} as a {@link ConfigurationSection}.
 */
@Tag("ConfigurationSection")
@SuppressWarnings({"PMD.JUnit5TestShouldBePackagePrivate", "PMD.JUnitAssertionsShouldIncludeMessage"})
public class MultiSectionConfigurationSectionWithConfigurationTest extends ConfigurationSectionBaseTest {

    @Override
    public Configuration getConfig() {
        final Configuration defaultConfig = super.getDefaultConfig();
        final Map<ConfigurationSection, String> configs = new HashMap<>();
        configs.put(defaultConfig, "config.yml");
        try {
            final MultiConfiguration multiConfiguration = new MultiSectionConfiguration(new ArrayList<>(configs.keySet()));
            final Configuration defaults = defaultConfig.getDefaults();
            assertNotNull(defaults);
            multiConfiguration.setDefaults(defaults);
            return multiConfiguration;
        } catch (final KeyConflictException e) {
            fail(e.resolvedMessage(configs), e);
        } catch (final InvalidConfigurationException e) {
            fail(e);
        }
        return null;
    }

    @Test
    @Override
    @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
    public void testGetKeysDeepFalse() {
        assertEquals("[boolean, booleanList, characterList, childSection, color, default, double, doubleList, existingSet, get, integer, integerList, item, list, location, long, mapList, object, offlinePlayer, section, string, stringList, vector]",
                config.getKeys(false).stream().sorted().toList().toString());

        final ConfigurationSection section = config.getConfigurationSection("childSection");
        assertNotNull(section);
        assertEquals(new HashSet<>(Collections.singletonList("nestedChildSection")), section.getKeys(false));
    }

    @Test
    @Override
    @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
    public void testGetKeysDeepTrue() {
        assertEquals("[boolean, booleanList, characterList, childSection, childSection.nestedChildSection, childSection.nestedChildSection.key, color, default, default.key, double, doubleList, existingSet, get, integer, integerList, item, list, location, long, mapList, object, offlinePlayer, section, section.key, string, stringList, vector]",
                config.getKeys(true).stream().sorted().toList().toString());

        final ConfigurationSection section = config.getConfigurationSection("childSection");
        assertNotNull(section);
        assertEquals(new HashSet<>(Arrays.asList("nestedChildSection", "nestedChildSection.key")), section.getKeys(true));
    }
}
