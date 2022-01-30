package org.betonquest.betonquest.api.bukkit.config.custom.multi;

import org.betonquest.betonquest.api.bukkit.config.util.ConfigurationSectionBaseTest;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is a test for {@link MultiConfiguration} as a {@link ConfigurationSection}.
 */
@SuppressWarnings({"PMD.JUnit5TestShouldBePackagePrivate", "PMD.JUnitAssertionsShouldIncludeMessage"})
public class MultiConfigurationSectionWithConfigurationTest extends ConfigurationSectionBaseTest {
    /**
     * Empty constructor
     */
    public MultiConfigurationSectionWithConfigurationTest() {
        super();
    }

    @Override
    public Configuration getConfig() {
        final Configuration defaultConfig = super.getDefaultConfig();
        try {
            final MultiConfiguration multiConfiguration = new MultiConfiguration(defaultConfig);
            multiConfiguration.setMultiDefaults(defaultConfig.getDefaults());
            return multiConfiguration;
        } catch (final KeyConflictException e) {
            final Map<ConfigurationSection, String> configs = new HashMap<>();
            configs.put(defaultConfig, "config.yml");
            fail(e.resolvedMessage(configs), e);
        } catch (final InvalidConfigurationException e) {
            fail(e);
        }
        return null;
    }

    private void assertThrowsUnmodifiableException(final Executable executable) {
        final Exception exception = assertThrows(UnsupportedOperationException.class, executable);
        assertEquals(MultiConfiguration.UNMODIFIABLE_MESSAGE, exception.getMessage());
    }

    @Test
    @Override
    public void testAddDefault() {
        assertThrowsUnmodifiableException(super::testAddDefault);
    }

    @Test
    @Override
    public void testAddDefaultOnExistingConfigPath() {
        assertThrowsUnmodifiableException(super::testAddDefaultOnExistingConfigPath);
    }

    @Test
    @Override
    public void testGetValuesDeepFalse() {
        final ConfigurationSection section = config.getConfigurationSection("childSection");
        assertNotNull(section);
        assertEquals("{nestedChildSection=MemorySection[path='childSection.nestedChildSection', root='MemoryConfiguration']}",
                section.getValues(false).toString());
    }

    @Test
    @Override
    @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
    public void testGetKeysDeepFalse() {
        assertEquals("[boolean, booleanList, characterList, childSection, color, default, double, doubleList, existingSet, get, integer, integerList, item, list, location, long, mapList, object, offlinePlayer, section, string, stringList, vector]",
                config.getKeys(false).stream().sorted().collect(Collectors.toList()).toString());

        final ConfigurationSection section = config.getConfigurationSection("childSection");
        assertNotNull(section);
        assertEquals(new HashSet<>(Collections.singletonList("nestedChildSection")), section.getKeys(false));
    }

    @Test
    @Override
    @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
    public void testGetKeysDeepTrue() {
        assertEquals("[boolean, booleanList, characterList, childSection, childSection.nestedChildSection, childSection.nestedChildSection.key, color, default, default.key, double, doubleList, existingSet, get, integer, integerList, item, list, location, long, mapList, object, offlinePlayer, section, section.key, string, stringList, vector]",
                config.getKeys(true).stream().sorted().collect(Collectors.toList()).toString());

        final ConfigurationSection section = config.getConfigurationSection("childSection");
        assertNotNull(section);
        assertEquals(new HashSet<>(Arrays.asList("nestedChildSection", "nestedChildSection.key")), section.getKeys(true));
    }
}
