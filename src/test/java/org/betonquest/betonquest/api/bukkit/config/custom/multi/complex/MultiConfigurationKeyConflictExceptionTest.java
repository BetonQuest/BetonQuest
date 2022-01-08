package org.betonquest.betonquest.api.bukkit.config.custom.multi.complex;

import org.betonquest.betonquest.api.bukkit.config.custom.multi.InvalidSubConfigurationException;
import org.betonquest.betonquest.api.bukkit.config.custom.multi.KeyConflictException;
import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiConfiguration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static com.ibm.icu.impl.Assert.fail;
import static org.junit.jupiter.api.Assertions.*;

/**
 * This is a test for the {@link MultiConfiguration} and it's thrown {@link KeyConflictException}s.
 */
@SuppressWarnings({"PMD.JUnitAssertionsShouldIncludeMessage", "PMD.AvoidDuplicateLiterals"})
class MultiConfigurationKeyConflictExceptionTest {
    /**
     * Config with key3 as value
     */
    public static final String CONFIG_STRING_1 = "section:\n"
            + "    key1: value1\n"
            + "    key3: value3-1\n"
            + "    key4: value4\n";
    /**
     * Config with key3 as value
     */
    public static final String CONFIG_STRING_2 = "section:\n"
            + "    key2: value2\n"
            + "    key3: value3-2\n"
            + "    key5: value5\n";
    /**
     * Config with key3 as section
     */
    public static final String CONFIG_STRING_3 = "section:\n"
            + "    key2: value2\n"
            + "    key3:\n"
            + "        key: value\n"
            + "    key5: value5\n";

    /**
     * Empty constructor
     */
    public MultiConfigurationKeyConflictExceptionTest() {
        super();
    }

    @Test
    void testKeyConflictException() {
        final Map<ConfigurationSection, String> configs = new HashMap<>();
        try {
            createConfig(configs, CONFIG_STRING_1, CONFIG_STRING_2);
        } catch (final KeyConflictException e) {
            assertNull(null, e.getMessage());
            assertEquals("You have conflicts in your configuration files:\n" +
                            "\n" +
                            "    The key 'section.key3' is defined multiple times in the following configs:\n" +
                            "        - Config-1\n" +
                            "        - Config-2\n"
                    , e.resolvedMessage(configs));
            return;
        } catch (final InvalidSubConfigurationException e) {
            fail(e);
        }
        fail("Expected an Exception!");
    }

    @Test
    void testPathConflictException() {
        final Map<ConfigurationSection, String> configs = new HashMap<>();
        try {
            createConfig(configs, CONFIG_STRING_1, CONFIG_STRING_3);
        } catch (final KeyConflictException e) {
            assertNull(null, e.getMessage());
            assertEquals("You have conflicts in your configuration files:\n" +
                            "\n" +
                            "    The key 'section.key3' in config 'Config-1' is a path with sub keys in at least one of the following configs:\n" +
                            "        - Config-2 with 'section.key3.key'\n"
                    , e.resolvedMessage(configs));
            return;
        } catch (final InvalidSubConfigurationException e) {
            fail(e);
        }
        fail("Expected an Exception!");
    }

    @Test
    void testKeyPathConflictException() {
        final Map<ConfigurationSection, String> configs = new HashMap<>();
        try {
            createConfig(configs, CONFIG_STRING_1, CONFIG_STRING_2, CONFIG_STRING_3);
        } catch (final KeyConflictException e) {
            assertNull(null, e.getMessage());
            assertEquals("You have conflicts in your configuration files:\n" +
                            "\n" +
                            "    The key 'section.key2' is defined multiple times in the following configs:\n" +
                            "        - Config-2\n" +
                            "        - Config-3\n" +
                            "    The key 'section.key3' is defined multiple times in the following configs:\n" +
                            "        - Config-1\n" +
                            "        - Config-2\n" +
                            "    The key 'section.key5' is defined multiple times in the following configs:\n" +
                            "        - Config-2\n" +
                            "        - Config-3\n" +
                            "\n" +
                            "    The key 'section.key3' in config 'Config-2' is a path with sub keys in at least one of the following configs:\n" +
                            "        - Config-1 with 'section.key3'\n" +
                            "        - Config-3 with 'section.key3.key'\n"
                    , e.resolvedMessage(configs));
            return;
        } catch (final InvalidSubConfigurationException e) {
            fail(e);
        }
        fail("Expected an Exception!");
    }

    private void createConfig(final Map<ConfigurationSection, String> configsMap, final String... configStrings) throws KeyConflictException, InvalidSubConfigurationException {
        final ConfigurationSection[] configs = new ConfigurationSection[configStrings.length];
        for (int i = 0; i < configStrings.length; i++) {
            final YamlConfiguration config = new YamlConfiguration();
            configs[i] = config;
            configsMap.put(config, "Config-" + (i + 1));

            try {
                config.loadFromString(configStrings[i]);
            } catch (final InvalidConfigurationException e) {
                fail(e);
            }
        }
        new MultiConfiguration(configs);
    }
}
