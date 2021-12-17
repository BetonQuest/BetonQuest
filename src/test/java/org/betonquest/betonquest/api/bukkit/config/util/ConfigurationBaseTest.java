package org.betonquest.betonquest.api.bukkit.config.util;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.MemoryConfiguration;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class tests all methods in the {@link Configuration} interface.
 * Additionally, the behaviour of the {@link MemoryConfiguration} is verified.
 * Therefore, this test should fail if the behaviour of the Bukkit API has changed.
 * <p>
 * This class can be used to test custom implementations of {@link Configuration}.
 * You only need to override methods with behaviours that differ from the default one.
 */
@SuppressWarnings({"PMD.JUnitAssertionsShouldIncludeMessage", "PMD.AvoidDuplicateLiterals"})
public class ConfigurationBaseTest extends AbstractConfigBaseTest<Configuration> implements ConfigurationTestInterface {
    /**
     * Empty constructor
     */
    public ConfigurationBaseTest() {
        super();
    }

    @Override
    public Configuration getConfig() {
        return getDefaultConfig();
    }

    @Test
    @Override
    public void testAddDefaultOnRootSection() {
        config.addDefault("default.add", "value");
        assertEquals("value", config.getString("default.add"));
    }

    @Test
    @Override
    public void testAddDefaultOnRootSectionOnExistingConfigPath() {
        config.addDefault("default.override", "first");
        config.addDefault("default.override", "second");
        assertEquals("second", config.getString("default.override"));
    }

    @Test
    @Override
    public void testAddDefaultsAsMap() {
        final Map<String, Object> defaultMap = new HashMap<>();
        defaultMap.put("default.one", 1);
        defaultMap.put("default.two", 2);
        config.addDefaults(defaultMap);
        assertEquals(1, config.getInt("default.one"));
        assertEquals(2, config.getInt("default.two"));
    }

    @Test
    @Override
    @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
    public void testAddDefaultsAsMapOnExistingConfigPath() {
        final Map<String, Object> defaultMap1 = new HashMap<>();
        defaultMap1.put("default.one", 1);
        defaultMap1.put("default.two", 2);
        config.addDefaults(defaultMap1);
        assertEquals(1, config.getInt("default.one"));
        assertEquals(2, config.getInt("default.two"));

        final Map<String, Object> defaultMap2 = new HashMap<>();
        defaultMap2.put("default.three", 3);
        defaultMap2.put("default.four", 4);
        config.addDefaults(defaultMap2);
        assertEquals(3, config.getInt("default.three"));
        assertEquals(4, config.getInt("default.four"));

        assertEquals(1, config.getInt("default.one"));
        assertEquals(2, config.getInt("default.two"));
    }

    @Test
    @Override
    public void testAddDefaultsAsConfiguration() {
        final Configuration defaultSection = new MemoryConfiguration();
        defaultSection.set("default.one", 1);
        defaultSection.set("default.two", 2);
        config.addDefaults(defaultSection);
        assertEquals(1, config.getInt("default.one"));
        assertEquals(2, config.getInt("default.two"));
    }

    @Test
    @Override
    @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
    public void testAddDefaultsAsConfigurationOnExistingConfigPath() {
        final Configuration defaultSection1 = new MemoryConfiguration();
        defaultSection1.set("default.one", 1);
        defaultSection1.set("default.two", 2);
        config.addDefaults(defaultSection1);
        assertEquals(1, config.getInt("default.one"));
        assertEquals(2, config.getInt("default.two"));

        final Configuration defaultSection2 = new MemoryConfiguration();
        defaultSection2.set("default.three", 3);
        defaultSection2.set("default.four", 4);
        config.addDefaults(defaultSection2);
        assertEquals(3, config.getInt("default.three"));
        assertEquals(4, config.getInt("default.four"));

        assertEquals(1, config.getInt("default.one"));
        assertEquals(2, config.getInt("default.two"));
    }

    @Test
    @Override
    public void testGetDefaults() {
        final Configuration defaults = config.getDefaults();
        assertNotNull(defaults);
        assertEquals("{default=MemorySection[path='default', root='MemoryConfiguration'], default.key=value}", defaults.getValues(true).toString());
    }

    @Test
    @Override
    @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
    public void testSetDefaults() {
        final Configuration defaultSection = new MemoryConfiguration();
        defaultSection.set("default.one", 1);
        defaultSection.set("default.two", 2);
        config.setDefaults(defaultSection);
        assertEquals(1, config.getInt("default.one"));
        assertEquals(2, config.getInt("default.two"));
        assertNull(config.get("default.key"));
    }

    @Test
    @Override
    @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
    public void testSetDefaultsOnExistingConfigPath() {
        final Configuration defaultSection1 = new MemoryConfiguration();
        defaultSection1.set("default.one", 1);
        defaultSection1.set("default.two", 2);
        config.setDefaults(defaultSection1);
        assertEquals(1, config.getInt("default.one"));
        assertEquals(2, config.getInt("default.two"));

        final Configuration defaultSection2 = new MemoryConfiguration();
        defaultSection2.set("default.three", 3);
        defaultSection2.set("default.four", 4);
        config.setDefaults(defaultSection2);
        assertEquals(3, config.getInt("default.three"));
        assertEquals(4, config.getInt("default.four"));

        assertNull(config.get("default.one"));
        assertNull(config.get("default.two"));
    }

    @Test
    @Override
    @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
    public void testOptions() {
        assertFalse(config.options().copyDefaults());
        config.options().copyDefaults(true);
        assertTrue(config.options().copyDefaults());

        assertEquals('.', config.options().pathSeparator());
        config.options().pathSeparator('-');
        assertEquals('-', config.options().pathSeparator());

    }
}
