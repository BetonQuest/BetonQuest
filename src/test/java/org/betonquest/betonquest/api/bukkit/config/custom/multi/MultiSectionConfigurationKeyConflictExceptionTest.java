package org.betonquest.betonquest.api.bukkit.config.custom.multi;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.ibm.icu.impl.Assert.fail;
import static org.junit.jupiter.api.Assertions.*;

/**
 * This is a test for the {@link MultiSectionConfiguration} and it's thrown {@link KeyConflictException}s.
 */
@Tag("ConfigurationSection")
@SuppressWarnings("PMD.UnitTestAssertionsShouldIncludeMessage")
class MultiSectionConfigurationKeyConflictExceptionTest {
    /**
     * Config with key3 as value
     */
    public static final String CONFIG_STRING_1 = """
            section:
                key1: value1
                key3: value3-1
                key4: value4
            """;

    /**
     * Config with key3 as value
     */
    public static final String CONFIG_STRING_2 = """
            section:
                key2: value2
                key3: value3-2
                key5: value5
            """;

    /**
     * Config with key3 as section
     */
    public static final String CONFIG_STRING_3 = """
            section:
                key2: value2
                key3:
                    key: value
                key5: value5
            """;

    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    @Test
    void testKeyConflictException() {
        final Map<ConfigurationSection, String> configs = new HashMap<>();
        try {
            createConfig(configs, CONFIG_STRING_1, CONFIG_STRING_2);
        } catch (final KeyConflictException e) {
            assertNull(e.getMessage());
            assertEquals("""
                    You have conflicts in your configuration files:

                        The key 'section.key3' is defined multiple times in the following configs:
                            - Config-1
                            - Config-2
                    """, e.resolvedMessage(configs));
            return;
        } catch (final InvalidSubConfigurationException e) {
            fail(e);
        }
        fail("Expected an Exception!");
    }

    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    @Test
    void testPathConflictException() {
        final Map<ConfigurationSection, String> configs = new HashMap<>();
        try {
            createConfig(configs, CONFIG_STRING_1, CONFIG_STRING_3);
        } catch (final KeyConflictException e) {
            assertNull(e.getMessage());
            assertEquals("""
                    You have conflicts in your configuration files:

                        The key 'section.key3' in config 'Config-1' is a path with sub keys in at least one of the following configs:
                            - Config-2 with 'section.key3.key'
                    """, e.resolvedMessage(configs));
            return;
        } catch (final InvalidSubConfigurationException e) {
            fail(e);
        }
        fail("Expected an Exception!");
    }

    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    @Test
    void testKeyPathConflictException() {
        final Map<ConfigurationSection, String> configs = new HashMap<>();
        try {
            createConfig(configs, CONFIG_STRING_1, CONFIG_STRING_2, CONFIG_STRING_3);
        } catch (final KeyConflictException e) {
            assertNull(e.getMessage());
            assertEquals("""
                    You have conflicts in your configuration files:

                        The key 'section.key2' is defined multiple times in the following configs:
                            - Config-2
                            - Config-3
                        The key 'section.key3' is defined multiple times in the following configs:
                            - Config-1
                            - Config-2
                        The key 'section.key5' is defined multiple times in the following configs:
                            - Config-2
                            - Config-3

                        The key 'section.key3' in config 'Config-1' is a path with sub keys in at least one of the following configs:
                            - Config-2 with 'section.key3'
                            - Config-3 with 'section.key3.key'
                    """, e.resolvedMessage(configs));
            return;
        } catch (final InvalidSubConfigurationException e) {
            fail(e);
        }
        fail("Expected an Exception!");
    }

    private void createConfig(final Map<ConfigurationSection, String> configsMap, final String... configStrings) throws KeyConflictException, InvalidSubConfigurationException {
        for (int i = 0; i < configStrings.length; i++) {
            final YamlConfiguration config = new YamlConfiguration();
            configsMap.put(config, "Config-" + (i + 1));
            try {
                config.loadFromString(configStrings[i]);
            } catch (final InvalidConfigurationException e) {
                fail(e);
            }
        }
        new MultiSectionConfiguration(new ArrayList<>(configsMap.keySet()));
    }
}
