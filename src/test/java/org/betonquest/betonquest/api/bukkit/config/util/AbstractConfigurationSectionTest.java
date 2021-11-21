package org.betonquest.betonquest.api.bukkit.config.util;

import org.bukkit.Color;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class AbstractConfigurationSectionTest implements ConfigurationSectionTestInterface {
    public ConfigurationSection getConfig() {
        ConfigurationSerialization.registerClass(TestObject.class);
        final Configuration config = YamlConfiguration.loadConfiguration(new File("src/test/resources/api/bukkit/config.yml"));
        final Configuration defaultSection = new MemoryConfiguration();
        defaultSection.set("default.key", "value");
        config.setDefaults(defaultSection);
        return config;
    }

    @Test
    @Override
    public void testGetKeys() {
        final ConfigurationSection config = getConfig();
        final ConfigurationSection section = config.getConfigurationSection("childSection");
        assertNotNull(section);
        assertEquals(new HashSet<>(Collections.singletonList("nestedChildSection")), section.getKeys(false));
        assertEquals(new HashSet<>(Arrays.asList("nestedChildSection", "nestedChildSection.key")), section.getKeys(true));
    }

    @Test
    @Override
    public void testGetValues() {
        final ConfigurationSection config = getConfig();
        final ConfigurationSection section = config.getConfigurationSection("childSection");
        assertNotNull(section);
        assertEquals("{nestedChildSection=MemorySection[path='childSection.nestedChildSection', root='YamlConfiguration']}",
                section.getValues(false).toString());
        assertEquals("[nestedChildSection, nestedChildSection.key]", section.getKeys(true).toString());
    }

    @Test
    @Override
    public void testContains() {
        final ConfigurationSection config = getConfig();
        assertTrue(config.contains("get"));
        assertTrue(config.contains("default.key", false));
    }

    @Test
    @Override
    public void testContainsOnInvalidConfigPath() {
        final ConfigurationSection config = getConfig();
        assertFalse(config.contains("get_invalid"));
        assertFalse(config.contains("default.key_invalid", false));
    }

    @Test
    @Override
    public void testContainsIgnoreDefault() {
        final ConfigurationSection config = getConfig();
        assertTrue(config.contains("get", true));
        assertFalse(config.contains("default.key", true));
    }

    @Test
    @Override
    public void testContainsIgnoreDefaultOnInvalidConfigPath() {
        final ConfigurationSection config = getConfig();
        assertFalse(config.contains("get_invalid", true));
        assertFalse(config.contains("default.key_invalid", true));
    }

    @Test
    @Override
    public void testIsSet() {
        final ConfigurationSection config = getConfig();
        assertTrue(config.isSet("get"));
    }

    @Test
    @Override
    public void testIsSetOnInvalidConfigPath() {
        final ConfigurationSection config = getConfig();
        assertFalse(config.isSet("get_invalid"));
    }

    @Test
    @Override
    public void testGetCurrentPath() {
        final ConfigurationSection config = getConfig();
        final ConfigurationSection nestedChild = config.getConfigurationSection("childSection.nestedChildSection");
        assertNotNull(nestedChild);
        assertEquals("childSection.nestedChildSection", nestedChild.getCurrentPath());
    }

    @Test
    @Override
    public void testGetName() {
        final ConfigurationSection config = getConfig();
        final ConfigurationSection nestedChild = config.getConfigurationSection("childSection.nestedChildSection");
        assertNotNull(nestedChild);
        assertEquals("nestedChildSection", nestedChild.getName());
    }

    @Test
    @Override
    public void testGetRoot() {
        final ConfigurationSection config = getConfig();
        final ConfigurationSection nestedChild = config.getConfigurationSection("childSection.nestedChildSection");
        assertNotNull(nestedChild);
        assertEquals(config, nestedChild.getRoot());
    }

    @Test
    @Override
    public void testGetParent() {
        final ConfigurationSection config = getConfig();
        final ConfigurationSection nestedChild = config.getConfigurationSection("childSection.nestedChildSection");
        assertNotNull(nestedChild);
        assertEquals(config.getConfigurationSection("childSection"), nestedChild.getParent());
    }

    @Test
    @Override
    public void testGet() {
        final ConfigurationSection config = getConfig();
        assertEquals("getValue", config.get("get"));
    }

    @Test
    @Override
    public void testGetOnInvalidConfigPath() {
        final ConfigurationSection config = getConfig();
        assertNull(config.get("get_invalid"));
    }

    @Test
    @Override
    public void testGetWithDefault() {
        final ConfigurationSection config = getConfig();
        assertEquals("getValue", config.get("get", "defaultValue"));
    }

    @Test
    @Override
    public void testGetWithDefaultOnInvalidConfigPath() {
        final ConfigurationSection config = getConfig();
        assertEquals("defaultValue", config.get("get_invalid", "defaultValue"));
    }

    @Test
    @Override
    public void testSet() {
        final ConfigurationSection config = getConfig();
        config.set("testSet", "setValue");
        assertEquals("setValue", config.get("testSet"));
    }

    @Test
    @Override
    public void testSetOnExistingConfigPath() {
        final ConfigurationSection config = getConfig();
        config.set("existingSet", "overriddenValue");
        assertEquals("overriddenValue", config.get("existingSet"));
    }

    @Test
    @Override
    public void testCreateSection() {
        final ConfigurationSection config = getConfig();
        final ConfigurationSection section = config.createSection("createdSection");
        assertNotNull(section);
    }

    @Test
    @Override
    public void testCreateSectionOnExistingConfigPath() {
        final ConfigurationSection config = getConfig();
        final ConfigurationSection section = config.createSection("createdSectionExist");
        section.set("key", "created value");
        final ConfigurationSection sectionRecreated = config.createSection("createdSectionExist");
        assertNotEquals(section, sectionRecreated);
        assertEquals("created value", section.getString("key"));
        assertNull(sectionRecreated.getString("key"));
        assertNull(config.getString("createdSectionExist.key"));
    }

    @Test
    @Override
    @SuppressWarnings("serial")
    public void testCreateSectionWithValues() {
        final ConfigurationSection config = getConfig();
        final ConfigurationSection section = config.createSection("createdSectionWithValues", new HashMap<String, Object>() {{
            put("one", 1);
            put("two", 2);
        }});
        assertNotNull(section);
        assertEquals(1, section.getInt("one"));
        assertEquals(2, section.getInt("two"));
    }

    @Test
    @Override
    @SuppressWarnings("serial")
    public void testCreateSectionWithValuesOnExistingConfigPath() {
        final ConfigurationSection config = getConfig();
        final ConfigurationSection section = config.createSection("createdSectionWithValuesExist", new HashMap<String, Object>() {{
            put("one", 1);
            put("two", 2);
        }});
        final ConfigurationSection sectionRecreated = config.createSection("createdSectionWithValuesExist", new HashMap<String, Object>() {{
            put("three", 3);
            put("four", 4);
        }});
        assertNotEquals(section, sectionRecreated);
        assertEquals(1, section.getInt("one"));
        assertEquals(2, section.getInt("two"));
        assertEquals(3, sectionRecreated.getInt("three"));
        assertEquals(4, sectionRecreated.getInt("four"));
        assertNull(config.getString("createdSectionWithValuesExist.one"));
        assertNull(config.getString("createdSectionWithValuesExist.two"));
        assertEquals(3, config.getInt("createdSectionWithValuesExist.three"));
        assertEquals(4, config.getInt("createdSectionWithValuesExist.four"));
    }

    @Test
    @Override
    public void testGetString() {
        final ConfigurationSection config = getConfig();
        assertEquals("Custom String", config.getString("string"));
    }

    @Test
    @Override
    public void testGetStringOnInvalidConfigPath() {
        final ConfigurationSection config = getConfig();
        assertNull(config.getString("string_invalid"));
    }

    @Test
    @Override
    public void testGetStringWithDefault() {
        final ConfigurationSection config = getConfig();
        assertEquals("Custom String", config.getString("string", "Custom String Default"));
    }

    @Test
    @Override
    public void testGetStringWithDefaultOnInvalidConfigPath() {
        final ConfigurationSection config = getConfig();
        assertEquals("Custom String Default", config.getString("string_invalid", "Custom String Default"));
    }

    @Test
    @Override
    public void testIsString() {
        final ConfigurationSection config = getConfig();
        assertTrue(config.isString("string"));
    }

    @Test
    @Override
    public void testIsStringOnInvalidConfigPath() {
        final ConfigurationSection config = getConfig();
        assertFalse(config.isString("string_invalid"));
    }

    @Test
    @Override
    public void testGetInt() {
        final ConfigurationSection config = getConfig();
        assertEquals(12345, config.getInt("integer"));
    }

    @Test
    @Override
    public void testGetIntOnInvalidConfigPath() {
        final ConfigurationSection config = getConfig();
        assertEquals(0, config.getInt("integer_invalid"));
    }

    @Test
    @Override
    public void testGetIntWithDefault() {
        final ConfigurationSection config = getConfig();
        assertEquals(12345, config.getInt("integer", 54321));
    }

    @Test
    @Override
    public void testGetIntWithDefaultOnInvalidConfigPath() {
        final ConfigurationSection config = getConfig();
        assertEquals(54321, config.getInt("integer_invalid", 54321));
    }

    @Test
    @Override
    public void testIsInt() {
        final ConfigurationSection config = getConfig();
        assertTrue(config.isInt("integer"));
    }

    @Test
    @Override
    public void testIsIntOnInvalidConfigPath() {
        final ConfigurationSection config = getConfig();
        assertFalse(config.isInt("integer_invalid"));
    }

    @Test
    @Override
    public void testGetBoolean() {
        final ConfigurationSection config = getConfig();
        assertTrue(config.getBoolean("boolean"));
    }

    @Test
    @Override
    public void testGetBooleanOnInvalidConfigPath() {
        final ConfigurationSection config = getConfig();
        assertFalse(config.getBoolean("boolean_invalid"));
    }

    @Test
    @Override
    public void testGetBooleanWithDefault() {
        final ConfigurationSection config = getConfig();
        assertTrue(config.getBoolean("boolean", false));
    }

    @Test
    @Override
    public void testGetBooleanWithDefaultOnInvalidConfigPath() {
        final ConfigurationSection config = getConfig();
        assertTrue(config.getBoolean("boolean_invalid", true));
    }

    @Test
    @Override
    public void testIsBoolean() {
        final ConfigurationSection config = getConfig();
        assertTrue(config.isBoolean("boolean"));
    }

    @Test
    @Override
    public void testIsBooleanOnInvalidConfigPath() {
        final ConfigurationSection config = getConfig();
        assertFalse(config.isBoolean("boolean_invalid"));
    }

    @Test
    @Override
    public void testGetDouble() {
        final ConfigurationSection config = getConfig();
        assertEquals(123.45, config.getDouble("double"));
    }

    @Test
    @Override
    public void testGetDoubleOnInvalidConfigPath() {
        final ConfigurationSection config = getConfig();
        assertEquals(0.0, config.getDouble("double_invalid"));
    }

    @Test
    @Override
    public void testGetDoubleWithDefault() {
        final ConfigurationSection config = getConfig();
        assertEquals(123.45, config.getDouble("double", 543.21));
    }

    @Test
    @Override
    public void testGetDoubleWithDefaultOnInvalidConfigPath() {
        final ConfigurationSection config = getConfig();
        assertEquals(543.21, config.getDouble("double_invalid", 543.21));
    }

    @Test
    @Override
    public void testIsDouble() {
        final ConfigurationSection config = getConfig();
        assertTrue(config.isDouble("double"));
    }

    @Test
    @Override
    public void testIsDoubleOnInvalidConfigPath() {
        final ConfigurationSection config = getConfig();
        assertFalse(config.isDouble("double_invalid"));
    }

    @Test
    @Override
    public void testGetLong() {
        final ConfigurationSection config = getConfig();
        assertEquals(9223372036854775807L, config.getLong("long"));
    }

    @Test
    @Override
    public void testGetLongOnInvalidConfigPath() {
        final ConfigurationSection config = getConfig();
        assertEquals(0L, config.getLong("long_invalid"));
    }

    @Test
    @Override
    public void testGetLongWithDefault() {
        final ConfigurationSection config = getConfig();
        assertEquals(9223372036854775807L, config.getLong("long", -9223372036854775808L));
    }

    @Test
    @Override
    public void testGetLongWithDefaultOnInvalidConfigPath() {
        final ConfigurationSection config = getConfig();
        assertEquals(-9223372036854775808L, config.getLong("long_invalid", -9223372036854775808L));
    }

    @Test
    @Override
    public void testIsLong() {
        final ConfigurationSection config = getConfig();
        assertTrue(config.isLong("long"));
    }

    @Test
    @Override
    public void testIsLongOnInvalidConfigPath() {
        final ConfigurationSection config = getConfig();
        assertFalse(config.isLong("long_invalid"));
    }

    @Test
    @Override
    public void testGetList() {
        final ConfigurationSection config = getConfig();
        assertEquals(Arrays.asList("One", 2, 3.0), config.getList("list"));
    }

    @Test
    @Override
    public void testGetListOnInvalidConfigPath() {
        final ConfigurationSection config = getConfig();
        assertNull(config.getList("list_invalid"));
    }

    @Test
    @Override
    public void testGetListWithDefault() {
        final ConfigurationSection config = getConfig();
        assertEquals(Arrays.asList("One", 2, 3.0), config.getList("list", Arrays.asList("Four", 5, 6.0)));
    }

    @Test
    @Override
    public void testGetListWithDefaultOnInvalidConfigPath() {
        final ConfigurationSection config = getConfig();
        assertEquals(Arrays.asList("Four", 5, 6.0), config.getList("list_invalid", Arrays.asList("Four", 5, 6.0)));
    }

    @Test
    @Override
    public void testIsList() {
        final ConfigurationSection config = getConfig();
        assertTrue(config.isList("list"));
    }

    @Test
    @Override
    public void testIsListOnInvalidConfigPath() {
        final ConfigurationSection config = getConfig();
        assertFalse(config.isList("list_invalid"));
    }

    @Test
    @Override
    public void testGetStringList() {
        final ConfigurationSection config = getConfig();
        assertEquals(Arrays.asList("One", "Two", "Three"), config.getStringList("stringList"));
    }

    @Test
    @Override
    public void testGetStringListOnInvalidConfigPath() {
        final ConfigurationSection config = getConfig();
        assertEquals(Collections.emptyList(), config.getStringList("stringList_invalid"));
    }

    @Test
    @Override
    public void testGetIntegerList() {
        final ConfigurationSection config = getConfig();
        assertEquals(Arrays.asList(1, 2, 3), config.getIntegerList("integerList"));
    }

    @Test
    @Override
    public void testGetIntegerListOnInvalidConfigPath() {
        final ConfigurationSection config = getConfig();
        assertEquals(Collections.emptyList(), config.getIntegerList("integerList_invalid"));
    }

    @Test
    @Override
    public void testGetBooleanList() {
        final ConfigurationSection config = getConfig();
        assertEquals(Arrays.asList(true, false, true), config.getBooleanList("booleanList"));
    }

    @Test
    @Override
    public void testGetBooleanListOnInvalidConfigPath() {
        final ConfigurationSection config = getConfig();
        assertEquals(Collections.emptyList(), config.getBooleanList("booleanList_invalid"));
    }

    @Test
    @Override
    public void testGetDoubleList() {
        final ConfigurationSection config = getConfig();
        assertEquals(Arrays.asList(1.1, 2.2, 3.3), config.getDoubleList("doubleList"));
    }

    @Test
    @Override
    public void testGetDoubleListOnInvalidConfigPath() {
        final ConfigurationSection config = getConfig();
        assertEquals(Collections.emptyList(), config.getDoubleList("doubleList_invalid"));
    }

    @Test
    @Override
    public void testGetFloatList() {
        final ConfigurationSection config = getConfig();
        assertEquals(Arrays.asList(1.1F, 2.2F, 3.3F), config.getFloatList("doubleList"));
    }

    @Test
    @Override
    public void testGetFloatListOnInvalidConfigPath() {
        final ConfigurationSection config = getConfig();
        assertEquals(Collections.emptyList(), config.getFloatList("doubleList_invalid"));
    }

    @Test
    @Override
    public void testGetLongList() {
        final ConfigurationSection config = getConfig();
        assertEquals(Arrays.asList(1L, 2L, 3L), config.getLongList("doubleList"));
    }

    @Test
    @Override
    public void testGetLongListOnInvalidConfigPath() {
        final ConfigurationSection config = getConfig();
        assertEquals(Collections.emptyList(), config.getFloatList("doubleList_invalid"));
    }

    @Test
    @Override
    public void testGetByteList() {
        final ConfigurationSection config = getConfig();
        assertEquals(Arrays.asList((byte) 1, (byte) 2, (byte) 3), config.getByteList("integerList"));
    }

    @Test
    @Override
    public void testGetByteListOnInvalidConfigPath() {
        final ConfigurationSection config = getConfig();
        assertEquals(Collections.emptyList(), config.getByteList("integerList_invalid"));
    }

    @Test
    @Override
    public void testGetCharacterList() {
        final ConfigurationSection config = getConfig();
        assertEquals(Arrays.asList('a', 'b', 'c'), config.getCharacterList("characterList"));
    }

    @Test
    @Override
    public void testGetCharacterListOnInvalidConfigPath() {
        final ConfigurationSection config = getConfig();
        assertEquals(Collections.emptyList(), config.getByteList("characterList_invalid"));
    }

    @Test
    @Override
    public void testGetShortList() {
        final ConfigurationSection config = getConfig();
        assertEquals(Arrays.asList((short) 1, (short) 2, (short) 3), config.getShortList("integerList"));
    }

    @Test
    @Override
    public void testGetShortListOnInvalidConfigPath() {
        final ConfigurationSection config = getConfig();
        assertEquals(Collections.emptyList(), config.getByteList("integerList_invalid"));
    }

    @Test
    @Override
    @SuppressWarnings("serial")
    public void testGetMapList() {
        final ConfigurationSection config = getConfig();
        assertEquals(Arrays.asList(new HashMap<String, Integer>() {{
                                       put("one", 1);
                                       put("two", 2);
                                   }},
                new HashMap<String, Integer>() {{
                    put("three", 3);
                    put("four", 4);
                }}), config.getMapList("mapList"));
    }

    @Test
    @Override
    public void testGetMapListOnInvalidConfigPath() {
        final ConfigurationSection config = getConfig();
        assertEquals(Collections.emptyList(), config.getMapList("mapList_invalid"));
    }

    @Test
    @Override
    public void testGetObject() {
        final ConfigurationSection config = getConfig();
        assertEquals(new TestObject("Test", 5, 555L), config.getObject("object", TestObject.class));
    }

    @Test
    @Override
    public void testGetObjectOnInvalidConfigPath() {
        final ConfigurationSection config = getConfig();
        assertNull(config.getObject("object_invalid", TestObject.class));
    }

    @Test
    @Override
    public void testGetObjectWithDefault() {
        final ConfigurationSection config = getConfig();
        assertEquals(new TestObject("Test", 5, 555L),
                config.getObject("object", TestObject.class, new TestObject("Test2", 4, 444L)));
    }

    @Test
    @Override
    public void testGetObjectWithDefaultOnInvalidConfigPath() {
        final ConfigurationSection config = getConfig();
        assertEquals(new TestObject("Test2", 4, 444L),
                config.getObject("object_invalid", TestObject.class, new TestObject("Test2", 4, 444L)));
    }

    @Test
    @Override
    public void testGetSerializable() {
        final ConfigurationSection config = getConfig();
        assertEquals(new TestObject("Test", 5, 555L), config.getSerializable("object", TestObject.class));
    }

    @Test
    @Override
    public void testGetSerializableOnInvalidConfigPath() {
        final ConfigurationSection config = getConfig();
        assertNull(config.getSerializable("object_invalid", TestObject.class));
    }

    @Test
    @Override
    public void testGetSerializableWithDefault() {
        final ConfigurationSection config = getConfig();
        assertEquals(new TestObject("Test", 5, 555L),
                config.getSerializable("object", TestObject.class, new TestObject("Test2", 4, 444L)));
    }

    @Test
    @Override
    public void testGetSerializableWithDefaultOnInvalidConfigPath() {
        final ConfigurationSection config = getConfig();
        assertEquals(new TestObject("Test2", 4, 444L),
                config.getSerializable("object_invalid", TestObject.class, new TestObject("Test2", 4, 444L)));
    }

    @Test
    @Override
    public void testGetVector() {
        final ConfigurationSection config = getConfig();
        assertEquals(new Vector(1, 2, 3), config.getVector("vector"));
    }

    @Test
    @Override
    public void testGetVectorOnInvalidConfigPath() {
        final ConfigurationSection config = getConfig();
        assertNull(config.getVector("vector_invalid"));
    }

    @Test
    @Override
    public void testGetVectorWithDefault() {
        final ConfigurationSection config = getConfig();
        assertEquals(new Vector(1, 2, 3), config.getVector("vector", new Vector(4, 5, 6)));
    }

    @Test
    @Override
    public void testGetVectorWithDefaultOnInvalidConfigPath() {
        final ConfigurationSection config = getConfig();
        assertEquals(new Vector(4, 5, 6), config.getVector("vector_invalid", new Vector(4, 5, 6)));
    }

    @Test
    @Override
    public void testIsVector() {
        final ConfigurationSection config = getConfig();
        assertTrue(config.isVector("vector"));
    }

    @Test
    @Override
    public void testIsVectorOnInvalidConfigPath() {
        final ConfigurationSection config = getConfig();
        assertFalse(config.isVector("vector_invalid"));
    }

    @Test
    @Override
    public void testGetColor() {
        final ConfigurationSection config = getConfig();
        assertEquals(Color.RED, config.getColor("color"));
    }

    @Test
    @Override
    public void testGetColorOnInvalidConfigPath() {
        final ConfigurationSection config = getConfig();
        assertNull(config.getColor("color_invalid"));
    }

    @Test
    @Override
    public void testGetColorWithDefault() {
        final ConfigurationSection config = getConfig();
        assertEquals(Color.RED, config.getColor("color", Color.GREEN));
    }

    @Test
    @Override
    public void testGetColorWithDefaultOnInvalidConfigPath() {
        final ConfigurationSection config = getConfig();
        assertEquals(Color.GREEN, config.getColor("color_invalid", Color.GREEN));
    }

    @Test
    @Override
    public void testIsColor() {
        final ConfigurationSection config = getConfig();
        assertTrue(config.isColor("color"));
    }

    @Test
    @Override
    public void testIsColorOnInvalidConfigPath() {
        final ConfigurationSection config = getConfig();
        assertFalse(config.isColor("color_invalid"));
    }

    @Test
    @Override
    public void testGetConfigurationSection() {
        final ConfigurationSection config = getConfig();
        final ConfigurationSection section = config.getConfigurationSection("section");
        assertNotNull(section);
        section.set("key", "new value");
        assertEquals("new value", section.getString("key"));
    }

    @Test
    @Override
    public void testGetConfigurationSectionOnInvalidConfigPath() {
        final ConfigurationSection config = getConfig();
        assertNull(config.getConfigurationSection("section_invalid"));
    }

    @Test
    @Override
    public void testIsConfigurationSection() {
        final ConfigurationSection config = getConfig();
        assertTrue(config.isConfigurationSection("section"));
    }

    @Test
    @Override
    public void testIsConfigurationSectionOnInvalidConfigPath() {
        final ConfigurationSection config = getConfig();
        assertFalse(config.isConfigurationSection("section_invalid"));
    }

    @Test
    @Override
    public void testGetDefaultSection() {
        final ConfigurationSection config = getConfig();
        final ConfigurationSection defaultSection = config.getDefaultSection();
        assertNotNull(defaultSection);
        assertEquals("value", defaultSection.getString("default.key"));
    }

    @Test
    @Override
    public void testGetDefaultSectionOnInvalidConfigPath() {
        final ConfigurationSection config = getConfig();
        assertNull(config.getConfigurationSection("default_invalid"));
    }

    @Test
    @Override
    public void testAddDefault() {
        final ConfigurationSection config = getConfig();
        config.addDefault("default.add", "value");
        assertEquals("value", config.getString("default.add"));
    }

    @Test
    @Override
    public void testAddDefaultOnExistingConfigPath() {
        final ConfigurationSection config = getConfig();
        config.addDefault("default.override", "first");
        config.addDefault("default.override", "second");
        assertEquals("second", config.getString("default.override"));
    }

    public static class TestObject implements ConfigurationSerializable {
        public final String name;
        public final int amount;
        public final long sum;

        public TestObject(final String name, final int amount, final long sum) {
            this.name = name;
            this.amount = amount;
            this.sum = sum;
        }

        @NotNull
        @SuppressWarnings("unused")
        public static TestObject deserialize(@NotNull final Map<String, Object> args) {
            return new TestObject((String) args.get("name"), (int) args.get("amount"), (int) args.get("sum"));
        }

        @Override
        public @NotNull
        Map<String, Object> serialize() {
            final Map<String, Object> map = new LinkedHashMap<>();
            map.put("name", name);
            map.put("amount", amount);
            map.put("sum", sum);
            return map;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            final TestObject that = (TestObject) o;
            return amount == that.amount && sum == that.sum && Objects.equals(name, that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, amount, sum);
        }
    }
}
