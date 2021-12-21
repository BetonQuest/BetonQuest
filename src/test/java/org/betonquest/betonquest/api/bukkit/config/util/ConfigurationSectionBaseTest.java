package org.betonquest.betonquest.api.bukkit.config.util;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class tests all methods in the {@link ConfigurationSection} interface.
 * Additionally, the behaviour of the {@link MemorySection} is verified.
 * Therefore, this test should fail if the behaviour of the Bukkit API has changed.
 * <p>
 * This class can be used to test custom implementations of {@link ConfigurationSection}.
 * You only need to override methods with behaviours that differ from the default one.
 */
@SuppressWarnings({"PMD.GodClass", "PMD.JUnitAssertionsShouldIncludeMessage", "PMD.AvoidDuplicateLiterals", "PMD.JUnit5TestShouldBePackagePrivate"})
public class ConfigurationSectionBaseTest extends AbstractConfigBaseTest<ConfigurationSection> implements ConfigurationSectionTestInterface {
    /**
     * Empty constructor
     */
    public ConfigurationSectionBaseTest() {
        super();
    }

    @Override
    public ConfigurationSection getConfig() {
        return getDefaultConfig();
    }

    @Test
    @Override
    @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
    public void testGetKeysDeepFalse() {
        assertEquals("[childSection, get, existingSet, string, integer, boolean, double, long, list, stringList, integerList, booleanList, doubleList, characterList, mapList, object, vector, color, section, location, item, offlinePlayer]",
                config.getKeys(false).toString());

        final ConfigurationSection section = config.getConfigurationSection("childSection");
        assertNotNull(section);
        assertEquals(new HashSet<>(Collections.singletonList("nestedChildSection")), section.getKeys(false));
    }

    @Test
    @Override
    @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
    public void testGetKeysDeepTrue() {
        assertEquals("[childSection, childSection.nestedChildSection, childSection.nestedChildSection.key, get, existingSet, string, integer, boolean, double, long, list, stringList, integerList, booleanList, doubleList, characterList, mapList, object, vector, color, section, section.key, location, item, offlinePlayer]",
                config.getKeys(true).toString());

        final ConfigurationSection section = config.getConfigurationSection("childSection");
        assertNotNull(section);
        assertEquals(new HashSet<>(Arrays.asList("nestedChildSection", "nestedChildSection.key")), section.getKeys(true));
    }

    @Test
    @Override
    public void testGetValuesDeepFalse() {
        final ConfigurationSection section = config.getConfigurationSection("childSection");
        assertNotNull(section);
        assertEquals("{nestedChildSection=MemorySection[path='childSection.nestedChildSection', root='YamlConfiguration']}",
                section.getValues(false).toString());
    }

    @Test
    @Override
    public void testGetValuesDeepTrue() {
        final ConfigurationSection section = config.getConfigurationSection("childSection");
        assertNotNull(section);
        assertEquals("[nestedChildSection, nestedChildSection.key]", section.getKeys(true).toString());
    }

    @Test
    @Override
    public void testContains() {
        assertTrue(config.contains("get"));
    }

    @Test
    @Override
    public void testContainsOnInvalidConfigPath() {
        assertFalse(config.contains("get_invalid"));
    }

    @Test
    @Override
    public void testContainsIgnoreDefaultFalse() {
        assertTrue(config.contains("get", false));
    }

    @Test
    @Override
    public void testContainsIgnoreDefaultFalseOnInvalidConfigPath() {
        assertFalse(config.contains("get_invalid", false));
    }

    @Test
    @Override
    public void testContainsIgnoreDefaultTrue() {
        assertTrue(config.contains("get", true));
    }

    @Test
    @Override
    public void testContainsIgnoreDefaultTrueOnInvalidConfigPath() {
        assertFalse(config.contains("get_invalid", true));
    }

    @Test
    @Override
    public void testContainsOnDefault() {
        assertTrue(config.contains("default.key"));
    }

    @Test
    @Override
    public void testContainsOnDefaultOnInvalidConfigPath() {
        assertFalse(config.contains("default.key_invalid"));
    }

    @Test
    @Override
    public void testContainsIgnoreDefaultFalseOnDefault() {
        assertTrue(config.contains("default.key", false));
    }

    @Test
    @Override
    public void testContainsIgnoreDefaultFalseOnDefaultOnInvalidConfigPath() {
        assertFalse(config.contains("default.key_invalid", false));
    }

    @Test
    @Override
    public void testContainsIgnoreDefaultTrueOnDefault() {
        assertFalse(config.contains("default.key", true));
    }

    @Test
    @Override
    public void testContainsIgnoreDefaultTrueOnDefaultOnInvalidConfigPath() {
        assertFalse(config.contains("default.key_invalid", true));
    }

    @Test
    @Override
    public void testIsSet() {
        assertTrue(config.isSet("get"));
    }

    @Test
    @Override
    public void testIsSetOnInvalidConfigPath() {
        assertFalse(config.isSet("get_invalid"));
    }

    @Test
    @Override
    @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
    public void testGetCurrentPath() {
        assertEquals("", config.getCurrentPath());
        final ConfigurationSection nestedChild = config.getConfigurationSection("childSection.nestedChildSection");
        assertNotNull(nestedChild);
        assertEquals("childSection.nestedChildSection", nestedChild.getCurrentPath());
    }

    @Test
    @Override
    @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
    public void testGetName() {
        assertEquals("", config.getName());
        final ConfigurationSection nestedChild = config.getConfigurationSection("childSection.nestedChildSection");
        assertNotNull(nestedChild);
        assertEquals("nestedChildSection", nestedChild.getName());
    }

    @Test
    @Override
    @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
    public void testGetRoot() {
        final ConfigurationSection root = config.getRoot();
        assertNotNull(root);
        assertEquals(config.getValues(true), root.getValues(true));

        final ConfigurationSection nestedChild = config.getConfigurationSection("childSection.nestedChildSection");
        assertNotNull(nestedChild);
        final ConfigurationSection nestedChildRoot = nestedChild.getRoot();
        assertNotNull(nestedChildRoot);
        assertEquals(config.getValues(true), nestedChildRoot.getValues(true));
    }

    @Test
    @Override
    @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
    public void testGetParent() {
        assertNull(config.getParent());

        final ConfigurationSection nestedChild = config.getConfigurationSection("childSection.nestedChildSection");
        assertNotNull(nestedChild);
        final ConfigurationSection nestedChildParent = nestedChild.getParent();
        assertNotNull(nestedChildParent);
        final ConfigurationSection parentSection = config.getConfigurationSection("childSection");
        assertNotNull(parentSection);
        assertEquals(parentSection.getValues(true), nestedChildParent.getValues(true));
    }

    @Test
    @Override
    public void testGet() {
        assertEquals("getValue", config.get("get"));
    }

    @Test
    @Override
    public void testGetOnInvalidConfigPath() {
        assertNull(config.get("get_invalid"));
    }

    @Test
    @Override
    public void testGetWithDefault() {
        assertEquals("getValue", config.get("get", "defaultValue"));
    }

    @Test
    @Override
    public void testGetWithDefaultOnInvalidConfigPath() {
        assertEquals("defaultValue", config.get("get_invalid", "defaultValue"));
    }

    @Test
    @Override
    public void testSet() {
        config.set("testSet", "setValue");
        assertEquals("setValue", config.get("testSet"));
    }

    @Test
    @Override
    public void testSetOnExistingConfigPath() {
        config.set("existingSet", "overriddenValue");
        assertEquals("overriddenValue", config.get("existingSet"));
    }

    @Test
    @Override
    public void testCreateSection() {
        final ConfigurationSection section = config.createSection("createdSection");
        assertNotNull(section);
    }

    @Test
    @Override
    @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
    public void testCreateSectionOnExistingConfigPath() {

        final ConfigurationSection section = config.createSection("createdSectionExist");
        section.set("key", "created value");
        assertEquals("created value", config.getString("createdSectionExist.key"));

        final ConfigurationSection sectionRecreated = config.createSection("createdSectionExist");
        assertNotEquals(section, sectionRecreated);

        assertEquals("created value", section.getString("key"));
        assertNull(sectionRecreated.getString("key"));
        assertNull(config.getString("createdSectionExist.key"));
    }

    @Test
    @Override
    @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
    public void testCreateSectionWithValues() {
        final Map<String, Object> sectionMap = new HashMap<>();
        sectionMap.put("one", 1);
        sectionMap.put("two", 2);
        final ConfigurationSection section = config.createSection("createdSectionWithValues", sectionMap);
        assertNotNull(section);
        assertEquals(1, section.getInt("one"));
        assertEquals(2, section.getInt("two"));
    }

    @Test
    @Override
    @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
    public void testCreateSectionWithValuesOnExistingConfigPath() {
        final Map<String, Object> sectionMap1 = new HashMap<>();
        sectionMap1.put("one", 1);
        sectionMap1.put("two", 2);
        final ConfigurationSection section = config.createSection("createdSectionWithValuesExist", sectionMap1);
        assertEquals(1, config.getInt("createdSectionWithValuesExist.one"));
        assertEquals(2, config.getInt("createdSectionWithValuesExist.two"));

        final Map<String, Object> sectionMap2 = new HashMap<>();
        sectionMap2.put("three", 3);
        sectionMap2.put("four", 4);
        final ConfigurationSection sectionRecreated = config.createSection("createdSectionWithValuesExist", sectionMap2);
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
        assertEquals("Custom String", config.getString("string"));
    }

    @Test
    @Override
    public void testGetStringOnInvalidConfigPath() {
        assertNull(config.getString("string_invalid"));
    }

    @Test
    @Override
    public void testGetStringWithDefault() {
        assertEquals("Custom String", config.getString("string", "Custom String Default"));
    }

    @Test
    @Override
    public void testGetStringWithDefaultOnInvalidConfigPath() {
        assertEquals("Custom String Default", config.getString("string_invalid", "Custom String Default"));
    }

    @Test
    @Override
    public void testIsString() {
        assertTrue(config.isString("string"));
    }

    @Test
    @Override
    public void testIsStringOnInvalidConfigPath() {
        assertFalse(config.isString("string_invalid"));
    }

    @Test
    @Override
    public void testGetInt() {
        assertEquals(12_345, config.getInt("integer"));
    }

    @Test
    @Override
    public void testGetIntOnInvalidConfigPath() {
        assertEquals(0, config.getInt("integer_invalid"));
    }

    @Test
    @Override
    public void testGetIntWithDefault() {
        assertEquals(12_345, config.getInt("integer", 54_321));
    }

    @Test
    @Override
    public void testGetIntWithDefaultOnInvalidConfigPath() {
        assertEquals(54_321, config.getInt("integer_invalid", 54_321));
    }

    @Test
    @Override
    public void testIsInt() {
        assertTrue(config.isInt("integer"));
    }

    @Test
    @Override
    public void testIsIntOnInvalidConfigPath() {
        assertFalse(config.isInt("integer_invalid"));
    }

    @Test
    @Override
    public void testGetBoolean() {
        assertTrue(config.getBoolean("boolean"));
    }

    @Test
    @Override
    public void testGetBooleanOnInvalidConfigPath() {
        assertFalse(config.getBoolean("boolean_invalid"));
    }

    @Test
    @Override
    public void testGetBooleanWithDefault() {
        assertTrue(config.getBoolean("boolean", false));
    }

    @Test
    @Override
    public void testGetBooleanWithDefaultOnInvalidConfigPath() {
        assertTrue(config.getBoolean("boolean_invalid", true));
    }

    @Test
    @Override
    public void testIsBoolean() {
        assertTrue(config.isBoolean("boolean"));
    }

    @Test
    @Override
    public void testIsBooleanOnInvalidConfigPath() {
        assertFalse(config.isBoolean("boolean_invalid"));
    }

    @Test
    @Override
    public void testGetDouble() {
        assertEquals(123.45, config.getDouble("double"));
    }

    @Test
    @Override
    public void testGetDoubleOnInvalidConfigPath() {
        assertEquals(0.0, config.getDouble("double_invalid"));
    }

    @Test
    @Override
    public void testGetDoubleWithDefault() {
        assertEquals(123.45, config.getDouble("double", 543.21));
    }

    @Test
    @Override
    public void testGetDoubleWithDefaultOnInvalidConfigPath() {
        assertEquals(543.21, config.getDouble("double_invalid", 543.21));
    }

    @Test
    @Override
    public void testIsDouble() {
        assertTrue(config.isDouble("double"));
    }

    @Test
    @Override
    public void testIsDoubleOnInvalidConfigPath() {
        assertFalse(config.isDouble("double_invalid"));
    }

    @Test
    @Override
    public void testGetLong() {
        assertEquals(Long.MAX_VALUE, config.getLong("long"));
    }

    @Test
    @Override
    public void testGetLongOnInvalidConfigPath() {
        assertEquals(0L, config.getLong("long_invalid"));
    }

    @Test
    @Override
    public void testGetLongWithDefault() {
        assertEquals(Long.MAX_VALUE, config.getLong("long", Long.MIN_VALUE));
    }

    @Test
    @Override
    public void testGetLongWithDefaultOnInvalidConfigPath() {
        assertEquals(Long.MIN_VALUE, config.getLong("long_invalid", Long.MIN_VALUE));
    }

    @Test
    @Override
    public void testIsLong() {
        assertTrue(config.isLong("long"));
    }

    @Test
    @Override
    public void testIsLongOnInvalidConfigPath() {
        assertFalse(config.isLong("long_invalid"));
    }

    @Test
    @Override
    public void testGetList() {
        assertEquals(Arrays.asList("One", 2, 3.0), config.getList("list"));
    }

    @Test
    @Override
    public void testGetListOnInvalidConfigPath() {
        assertNull(config.getList("list_invalid"));
    }

    @Test
    @Override
    public void testGetListWithDefault() {
        assertEquals(Arrays.asList("One", 2, 3.0), config.getList("list", Arrays.asList("Four", 5, 6.0)));
    }

    @Test
    @Override
    public void testGetListWithDefaultOnInvalidConfigPath() {
        assertEquals(Arrays.asList("Four", 5, 6.0), config.getList("list_invalid", Arrays.asList("Four", 5, 6.0)));
    }

    @Test
    @Override
    public void testIsList() {
        assertTrue(config.isList("list"));
    }

    @Test
    @Override
    public void testIsListOnInvalidConfigPath() {
        assertFalse(config.isList("list_invalid"));
    }

    @Test
    @Override
    public void testGetStringList() {
        assertEquals(Arrays.asList("One", "Two", "Three"), config.getStringList("stringList"));
    }

    @Test
    @Override
    public void testGetStringListOnInvalidConfigPath() {
        assertEquals(Collections.emptyList(), config.getStringList("stringList_invalid"));
    }

    @Test
    @Override
    public void testGetIntegerList() {
        assertEquals(Arrays.asList(1, 2, 3), config.getIntegerList("integerList"));
    }

    @Test
    @Override
    public void testGetIntegerListOnInvalidConfigPath() {
        assertEquals(Collections.emptyList(), config.getIntegerList("integerList_invalid"));
    }

    @Test
    @Override
    public void testGetBooleanList() {
        assertEquals(Arrays.asList(true, false, true), config.getBooleanList("booleanList"));
    }

    @Test
    @Override
    public void testGetBooleanListOnInvalidConfigPath() {
        assertEquals(Collections.emptyList(), config.getBooleanList("booleanList_invalid"));
    }

    @Test
    @Override
    public void testGetDoubleList() {
        assertEquals(Arrays.asList(1.1, 2.2, 3.3), config.getDoubleList("doubleList"));
    }

    @Test
    @Override
    public void testGetDoubleListOnInvalidConfigPath() {
        assertEquals(Collections.emptyList(), config.getDoubleList("doubleList_invalid"));
    }

    @Test
    @Override
    public void testGetFloatList() {
        assertEquals(Arrays.asList(1.1F, 2.2F, 3.3F), config.getFloatList("doubleList"));
    }

    @Test
    @Override
    public void testGetFloatListOnInvalidConfigPath() {
        assertEquals(Collections.emptyList(), config.getFloatList("doubleList_invalid"));
    }

    @Test
    @Override
    public void testGetLongList() {
        assertEquals(Arrays.asList(1L, 2L, 3L), config.getLongList("integerList"));
    }

    @Test
    @Override
    public void testGetLongListOnInvalidConfigPath() {
        assertEquals(Collections.emptyList(), config.getFloatList("integerList_invalid"));
    }

    @Test
    @Override
    public void testGetByteList() {
        assertEquals(Arrays.asList((byte) 1, (byte) 2, (byte) 3), config.getByteList("integerList"));
    }

    @Test
    @Override
    public void testGetByteListOnInvalidConfigPath() {
        assertEquals(Collections.emptyList(), config.getByteList("integerList_invalid"));
    }

    @Test
    @Override
    public void testGetCharacterList() {
        assertEquals(Arrays.asList('a', 'b', 'c'), config.getCharacterList("characterList"));
    }

    @Test
    @Override
    public void testGetCharacterListOnInvalidConfigPath() {
        assertEquals(Collections.emptyList(), config.getCharacterList("characterList_invalid"));
    }

    @Test
    @Override
    public void testGetShortList() {
        assertEquals(Arrays.asList((short) 1, (short) 2, (short) 3), config.getShortList("integerList"));
    }

    @Test
    @Override
    public void testGetShortListOnInvalidConfigPath() {
        assertEquals(Collections.emptyList(), config.getByteList("integerList_invalid"));
    }

    @Test
    @Override
    public void testGetMapList() {
        final List<Map<String, Integer>> mapList = new ArrayList<>();
        mapList.add(new HashMap<>());
        mapList.add(new HashMap<>());
        mapList.get(0).put("one", 1);
        mapList.get(0).put("two", 2);
        mapList.get(1).put("three", 3);
        mapList.get(1).put("four", 4);

        assertEquals(mapList, config.getMapList("mapList"));
    }

    @Test
    @Override
    public void testGetMapListOnInvalidConfigPath() {
        assertEquals(Collections.emptyList(), config.getMapList("mapList_invalid"));
    }

    @Test
    @Override
    public void testGetObject() {
        assertEquals(new TestObject("Test", 5, 555L), config.getObject("object", TestObject.class));
    }

    @Test
    @Override
    public void testGetObjectOnInvalidConfigPath() {
        assertNull(config.getObject("object_invalid", TestObject.class));
    }

    @Test
    @Override
    public void testGetObjectWithDefault() {
        assertEquals(new TestObject("Test", 5, 555L),
                config.getObject("object", TestObject.class, new TestObject("Test2", 4, 444L)));
    }

    @Test
    @Override
    public void testGetObjectWithDefaultOnInvalidConfigPath() {
        assertEquals(new TestObject("Test2", 4, 444L),
                config.getObject("object_invalid", TestObject.class, new TestObject("Test2", 4, 444L)));
    }

    @Test
    @Override
    public void testGetSerializable() {
        assertEquals(new TestObject("Test", 5, 555L), config.getSerializable("object", TestObject.class));
    }

    @Test
    @Override
    public void testGetSerializableOnInvalidConfigPath() {
        assertNull(config.getSerializable("object_invalid", TestObject.class));
    }

    @Test
    @Override
    public void testGetSerializableWithDefault() {
        assertEquals(new TestObject("Test", 5, 555L),
                config.getSerializable("object", TestObject.class, new TestObject("Test2", 4, 444L)));
    }

    @Test
    @Override
    public void testGetSerializableWithDefaultOnInvalidConfigPath() {
        assertEquals(new TestObject("Test2", 4, 444L),
                config.getSerializable("object_invalid", TestObject.class, new TestObject("Test2", 4, 444L)));
    }

    @Test
    @Override
    public void testGetVector() {
        assertEquals(new Vector(1, 2, 3), config.getVector("vector"));
    }

    @Test
    @Override
    public void testGetVectorOnInvalidConfigPath() {
        assertNull(config.getVector("vector_invalid"));
    }

    @Test
    @Override
    public void testGetVectorWithDefault() {
        assertEquals(new Vector(1, 2, 3), config.getVector("vector", new Vector(4, 5, 6)));
    }

    @Test
    @Override
    public void testGetVectorWithDefaultOnInvalidConfigPath() {
        assertEquals(new Vector(4, 5, 6), config.getVector("vector_invalid", new Vector(4, 5, 6)));
    }

    @Test
    @Override
    public void testIsVector() {
        assertTrue(config.isVector("vector"));
    }

    @Test
    @Override
    public void testIsVectorOnInvalidConfigPath() {
        assertFalse(config.isVector("vector_invalid"));
    }

    @Test
    @Override
    public void testGetOfflinePlayer() {
        final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString("eba17d33-959d-42a7-a4d9-e9aebef5969e"));
        final OfflinePlayer player = config.getOfflinePlayer("offlinePlayer");
        assertNotNull(player);
        assertEquals(offlinePlayer.getUniqueId(), player.getUniqueId());
    }

    @Test
    @Override
    public void testGetOfflinePlayerOnInvalidConfigPath() {
        assertNull(config.getOfflinePlayer("offlinePlayer_invalid"));
    }

    @Test
    @Override
    public void testGetOfflinePlayerWithDefault() {
        final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString("eba17d33-959d-42a7-a4d9-e9aebef5969e"));
        final OfflinePlayer offlinePlayerDefault = Bukkit.getOfflinePlayer(UUID.fromString("5179617b-8418-4099-8773-37a4ac587dd8"));
        final OfflinePlayer player = config.getOfflinePlayer("offlinePlayer", offlinePlayerDefault);
        assertNotNull(player);
        assertEquals(offlinePlayer.getUniqueId(), player.getUniqueId());
    }

    @Test
    @Override
    public void testGetOfflinePlayerWithDefaultOnInvalidConfigPath() {
        final OfflinePlayer offlinePlayerDefault = Bukkit.getOfflinePlayer(UUID.fromString("5179617b-8418-4099-8773-37a4ac587dd8"));
        final OfflinePlayer player = config.getOfflinePlayer("offlinePlayer_invalid", offlinePlayerDefault);
        assertNotNull(player);
        assertEquals(offlinePlayerDefault.getUniqueId(), player.getUniqueId());
    }

    @Test
    @Override
    public void testIsOfflinePlayer() {
        assertTrue(config.isOfflinePlayer("offlinePlayer"));
    }

    @Test
    @Override
    public void testIsOfflinePlayerOnInvalidConfigPath() {
        assertFalse(config.isOfflinePlayer("offlinePlayer_invalid"));
    }

    @Test
    @Override
    public void testGetItemStack() {
        final ItemStack item = new ItemStack(Material.BONE, 42);
        assertEquals(item, config.getItemStack("item"));
    }

    @Test
    @Override
    public void testGetItemStackOnInvalidConfigPath() {
        assertNull(config.getItemStack("item_invalid"));
    }

    @Test
    @Override
    public void testGetItemStackWithDefault() {
        final ItemStack item = new ItemStack(Material.BONE, 42);
        final ItemStack itemDefault = new ItemStack(Material.BONE_MEAL, 12);
        assertEquals(item, config.getItemStack("item", itemDefault));
    }

    @Test
    @Override
    public void testGetItemStackWithDefaultOnInvalidConfigPath() {
        final ItemStack itemDefault = new ItemStack(Material.BONE_MEAL, 12);
        assertEquals(itemDefault, config.getItemStack("item_invalid", itemDefault));
    }

    @Test
    @Override
    public void testIsItemStack() {
        assertTrue(config.isItemStack("item"));
    }

    @Test
    @Override
    public void testIsItemStackOnInvalidConfigPath() {
        assertFalse(config.isItemStack("item_invalid"));
    }

    @Test
    @Override
    public void testGetColor() {
        assertEquals(Color.RED, config.getColor("color"));
    }

    @Test
    @Override
    public void testGetColorOnInvalidConfigPath() {
        assertNull(config.getColor("color_invalid"));
    }

    @Test
    @Override
    public void testGetColorWithDefault() {
        assertEquals(Color.RED, config.getColor("color", Color.GREEN));
    }

    @Test
    @Override
    public void testGetColorWithDefaultOnInvalidConfigPath() {
        assertEquals(Color.GREEN, config.getColor("color_invalid", Color.GREEN));
    }

    @Test
    @Override
    public void testIsColor() {
        assertTrue(config.isColor("color"));
    }

    @Test
    @Override
    public void testIsColorOnInvalidConfigPath() {
        assertFalse(config.isColor("color_invalid"));
    }

    @Test
    @Override
    public void testGetLocation() {
        final Location location = new Location(Bukkit.getWorld("Test"), 1, 2, 3, 4, 5);
        assertEquals(location, config.getLocation("location"));
    }

    @Test
    @Override
    public void testGetLocationOnInvalidConfigPath() {
        assertNull(config.getLocation("location_invalid"));
    }

    @Test
    @Override
    public void testGetLocationWithDefault() {
        final Location location = new Location(Bukkit.getWorld("Test"), 1, 2, 3, 4, 5);
        final Location locationDefault = new Location(Bukkit.getWorld("TestInvalid"), 1, 2, 3, 4, 5);
        assertEquals(location, config.getLocation("location", locationDefault));
    }

    @Test
    @Override
    public void testGetLocationWithDefaultOnInvalidConfigPath() {
        final Location locationDefault = new Location(Bukkit.getWorld("TestInvalid"), 1, 2, 3, 4, 5);
        assertEquals(locationDefault, config.getLocation("location_invalid", locationDefault));
    }

    @Test
    @Override
    public void testIsLocation() {
        assertTrue(config.isLocation("location"));
    }

    @Test
    @Override
    public void testIsLocationOnInvalidConfigPath() {
        assertFalse(config.isLocation("location_invalid"));
    }

    @Test
    @Override
    public void testGetConfigurationSection() {
        final ConfigurationSection section = config.getConfigurationSection("section");
        assertNotNull(section);
        assertEquals("value", section.getString("key"));
    }

    @Test
    @Override
    public void testGetConfigurationSectionOnInvalidConfigPath() {
        assertNull(config.getConfigurationSection("section_invalid"));
    }

    @Test
    @Override
    public void testIsConfigurationSection() {
        assertTrue(config.isConfigurationSection("section"));
    }

    @Test
    @Override
    public void testIsConfigurationSectionOnInvalidConfigPath() {
        assertFalse(config.isConfigurationSection("section_invalid"));
    }

    @Test
    @Override
    public void testGetDefaultSection() {
        final ConfigurationSection defaultSection = config.getDefaultSection();
        assertNotNull(defaultSection);
        assertEquals("value", defaultSection.getString("default.key"));
    }

    @Test
    @Override
    public void testGetDefaultSectionOnInvalidConfigPath() {
        assertNull(config.getConfigurationSection("default_invalid"));
    }

    @Test
    @Override
    @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
    public void testAddDefault() {
        config.addDefault("default.add", "value");
        assertEquals("value", config.getString("default.add"));
    }

    @Test
    @Override
    @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
    public void testAddDefaultOnExistingConfigPath() {
        config.addDefault("default.override", "first");
        config.addDefault("default.override", "second");
        assertEquals("second", config.getString("default.override"));
    }
}
