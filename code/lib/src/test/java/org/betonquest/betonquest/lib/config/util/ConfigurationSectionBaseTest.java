package org.betonquest.betonquest.lib.config.util;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.MemorySection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * This class tests all methods in the {@link ConfigurationSection} interface.
 * Additionally, the behaviour of the {@link MemorySection} is verified.
 * Therefore, this test should fail if the behaviour of the Bukkit API has changed.
 * <p>
 * This class can be used to test custom implementations of {@link ConfigurationSection}.
 * You only need to override methods with behaviours that differ from the default one.
 */
@Tag("ConfigurationSection")
@SuppressWarnings({"PMD.GodClass", "PMD.UnitTestAssertionsShouldIncludeMessage", "PMD.JUnitJupiterTestShouldBePackagePrivate",
        "PMD.CyclomaticComplexity", "PMD.ExcessivePublicCount"})
public abstract class ConfigurationSectionBaseTest extends AbstractConfigBaseTest<ConfigurationSection> implements ConfigurationSectionInterfaceTest {

    public ConfigurationSectionBaseTest(final ConfigurationSection config) {
        super(config);
    }

    @Test
    @Override
    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    public void get_keys_deep_false() {
        assertEquals("[boolean, booleanList, characterList, childSection, color, double, doubleList, existingSet, get, integer, integerList, item, list, location, long, mapList, object, offlinePlayer, section, string, stringList, vector]",
                config.getKeys(false).stream().sorted().toList().toString());

        final ConfigurationSection section = config.getConfigurationSection("childSection");
        assertNotNull(section);
        assertEquals(new HashSet<>(Collections.singletonList("nestedChildSection")), section.getKeys(false));
    }

    @Test
    @Override
    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    public void get_keys_deep_true() {
        assertEquals("[boolean, booleanList, characterList, childSection, childSection.nestedChildSection, childSection.nestedChildSection.key, color, double, doubleList, existingSet, get, integer, integerList, item, list, location, long, mapList, object, offlinePlayer, section, section.key, string, stringList, vector]",
                config.getKeys(true).stream().sorted().toList().toString());

        final ConfigurationSection section = config.getConfigurationSection("childSection");
        assertNotNull(section);
        assertEquals(new HashSet<>(Arrays.asList("nestedChildSection", "nestedChildSection.key")), section.getKeys(true));
    }

    @Test
    @Override
    public void get_values_deep_false() {
        final ConfigurationSection section = config.getConfigurationSection("childSection");
        assertNotNull(section);
        final Pattern pattern = Pattern.compile(Pattern.quote("{nestedChildSection=") + "\\w+"
                + Pattern.quote("[path='childSection.nestedChildSection', root='") + "\\w+"
                + Pattern.quote("']}"));
        final String sectionString = section.getValues(false).toString();
        final Matcher matcher = pattern.matcher(sectionString);
        assertTrue(matcher.matches(), "Didn't match regex '%s'\nActual string '%s'".formatted(pattern, sectionString));
    }

    @Test
    @Override
    public void get_values_deep_true() {
        final ConfigurationSection section = config.getConfigurationSection("childSection");
        assertNotNull(section);
        final Pattern pattern = Pattern.compile(Pattern.quote("{nestedChildSection=") + "\\w+"
                + Pattern.quote("[path='childSection.nestedChildSection', root='") + "\\w+"
                + Pattern.quote("'], nestedChildSection.key=value}"));
        final String sectionString = section.getValues(true).toString();
        final Matcher matcher = pattern.matcher(sectionString);
        assertTrue(matcher.matches(), "Didn't match regex '%s'\nActual string '%s'".formatted(pattern, sectionString));
    }

    @Test
    @Override
    public void contains() {
        assertTrue(config.contains("get"));
    }

    @Test
    @Override
    public void contains_on_invalid_config_path() {
        assertFalse(config.contains("get_invalid"));
    }

    @Test
    @Override
    public void contains_ignore_default_false() {
        assertTrue(config.contains("get", false));
    }

    @Test
    @Override
    public void contains_ignore_default_false_on_invalid_config_path() {
        assertFalse(config.contains("get_invalid", false));
    }

    @Test
    @Override
    public void contains_ignore_default_true() {
        assertTrue(config.contains("get", true));
    }

    @Test
    @Override
    public void contains_ignore_default_true_on_invalid_config_path() {
        assertFalse(config.contains("get_invalid", true));
    }

    @Test
    @Override
    public void contains_on_default() {
        assertTrue(config.contains("default.key"));
    }

    @Test
    @Override
    public void contains_on_default_on_invalid_config_path() {
        assertFalse(config.contains("default.key_invalid"));
    }

    @Test
    @Override
    public void contains_ignore_default_false_on_default() {
        assertTrue(config.contains("default.key", false));
    }

    @Test
    @Override
    public void contains_ignore_default_false_on_default_on_invalid_config_path() {
        assertFalse(config.contains("default.key_invalid", false));
    }

    @Test
    @Override
    public void contains_ignore_default_true_on_default() {
        assertFalse(config.contains("default.key", true));
    }

    @Test
    @Override
    public void contains_ignore_default_true_on_default_on_invalid_config_path() {
        assertFalse(config.contains("default.key_invalid", true));
    }

    @Test
    @Override
    public void is_set() {
        assertTrue(config.isSet("get"));
    }

    @Test
    @Override
    public void is_set_on_invalid_config_path() {
        assertFalse(config.isSet("get_invalid"));
    }

    @Test
    @Override
    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    public void get_current_path() {
        assertEquals("", config.getCurrentPath());
        final ConfigurationSection nestedChild = config.getConfigurationSection("childSection.nestedChildSection");
        assertNotNull(nestedChild);
        assertEquals("childSection.nestedChildSection", nestedChild.getCurrentPath());
    }

    @Test
    @Override
    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    public void get_name() {
        assertEquals("", config.getName());
        final ConfigurationSection nestedChild = config.getConfigurationSection("childSection.nestedChildSection");
        assertNotNull(nestedChild);
        assertEquals("nestedChildSection", nestedChild.getName());
    }

    @Test
    @Override
    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    public void get_root() {
        final ConfigurationSection root = config.getRoot();
        assertNotNull(root);
        assertEquals(config.getValues(true).toString(), root.getValues(true).toString());

        final ConfigurationSection nestedChild = config.getConfigurationSection("childSection.nestedChildSection");
        assertNotNull(nestedChild);
        final ConfigurationSection nestedChildRoot = nestedChild.getRoot();
        assertNotNull(nestedChildRoot);
        assertEquals(config.getValues(true).toString(), nestedChildRoot.getValues(true).toString());
    }

    @Test
    @Override
    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    public void get_parent() {
        assertNull(config.getParent());

        final ConfigurationSection nestedChild = config.getConfigurationSection("childSection.nestedChildSection");
        assertNotNull(nestedChild);
        final ConfigurationSection nestedChildParent = nestedChild.getParent();
        assertNotNull(nestedChildParent);
        final ConfigurationSection parentSection = config.getConfigurationSection("childSection");
        assertNotNull(parentSection);
        assertEquals(parentSection.getValues(true).toString(), nestedChildParent.getValues(true).toString());
    }

    @Test
    @Override
    public void get() {
        assertEquals("getValue", config.get("get"));
    }

    @Test
    @Override
    public void get_on_invalid_config_path() {
        assertNull(config.get("get_invalid"));
    }

    @Test
    @Override
    public void get_with_default() {
        assertEquals("getValue", config.get("get", "defaultValue"));
    }

    @Test
    @Override
    public void get_with_default_on_invalid_config_path() {
        assertEquals("defaultValue", config.get("get_invalid", "defaultValue"));
    }

    @Test
    @Override
    public void set() {
        config.set("testSet", "setValue");
        assertEquals("setValue", config.get("testSet"));
    }

    @Test
    @Override
    public void set_on_existing_config_path() {
        config.set("existingSet", "overriddenValue");
        assertEquals("overriddenValue", config.get("existingSet"));
    }

    @Test
    @Override
    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    public void set_section_overwriting_existing() {
        assertTrue(config.isSet("childSection"));
        final MemoryConfiguration configuration = new MemoryConfiguration();
        configuration.set("test", "test");
        config.set("childSection", configuration);
        final ConfigurationSection childSection = config.getConfigurationSection("childSection");
        assertNotNull(childSection);
        assertEquals("[test]", childSection.getKeys(true).toString());
    }

    @Test
    @Override
    public void set_existing_section_to_null() {
        assertTrue(config.isSet("childSection"));
        config.set("childSection", null);
        assertFalse(config.isSet("childSection"));
    }

    @Test
    @Override
    public void create_section() {
        final ConfigurationSection section = config.createSection("createdSection");
        assertNotNull(section);
    }

    @Test
    @Override
    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    public void create_section_on_existing_config_path() {
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
    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    public void create_section_with_values() {
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
    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    public void create_section_with_values_on_existing_config_path() {
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
    public void get_string() {
        assertEquals("Custom String", config.getString("string"));
    }

    @Test
    @Override
    public void get_string_on_invalid_config_path() {
        assertNull(config.getString("string_invalid"));
    }

    @Test
    @Override
    public void get_string_with_default() {
        assertEquals("Custom String", config.getString("string", "Custom String Default"));
    }

    @Test
    @Override
    public void get_string_with_default_on_invalid_config_path() {
        assertEquals("Custom String Default", config.getString("string_invalid", "Custom String Default"));
    }

    @Test
    @Override
    public void is_string() {
        assertTrue(config.isString("string"));
    }

    @Test
    @Override
    public void is_string_on_invalid_config_path() {
        assertFalse(config.isString("string_invalid"));
    }

    @Test
    @Override
    public void get_int() {
        assertEquals(12_345, config.getInt("integer"));
    }

    @Test
    @Override
    public void get_int_on_invalid_config_path() {
        assertEquals(0, config.getInt("integer_invalid"));
    }

    @Test
    @Override
    public void get_int_with_default() {
        assertEquals(12_345, config.getInt("integer", 54_321));
    }

    @Test
    @Override
    public void get_int_with_default_on_invalid_config_path() {
        assertEquals(54_321, config.getInt("integer_invalid", 54_321));
    }

    @Test
    @Override
    public void is_int() {
        assertTrue(config.isInt("integer"));
    }

    @Test
    @Override
    public void is_int_on_invalid_config_path() {
        assertFalse(config.isInt("integer_invalid"));
    }

    @Test
    @Override
    public void get_boolean() {
        assertTrue(config.getBoolean("boolean"));
    }

    @Test
    @Override
    public void get_boolean_on_invalid_config_path() {
        assertFalse(config.getBoolean("boolean_invalid"));
    }

    @Test
    @Override
    public void get_boolean_with_default() {
        assertTrue(config.getBoolean("boolean", false));
    }

    @Test
    @Override
    public void get_boolean_with_default_on_invalid_config_path() {
        assertTrue(config.getBoolean("boolean_invalid", true));
    }

    @Test
    @Override
    public void is_boolean() {
        assertTrue(config.isBoolean("boolean"));
    }

    @Test
    @Override
    public void is_boolean_on_invalid_config_path() {
        assertFalse(config.isBoolean("boolean_invalid"));
    }

    @Test
    @Override
    public void get_double() {
        assertEquals(123.45, config.getDouble("double"));
    }

    @Test
    @Override
    public void get_double_on_invalid_config_path() {
        assertEquals(0.0, config.getDouble("double_invalid"));
    }

    @Test
    @Override
    public void get_double_with_default() {
        assertEquals(123.45, config.getDouble("double", 543.21));
    }

    @Test
    @Override
    public void get_double_with_default_on_invalid_config_path() {
        assertEquals(543.21, config.getDouble("double_invalid", 543.21));
    }

    @Test
    @Override
    public void is_double() {
        assertTrue(config.isDouble("double"));
    }

    @Test
    @Override
    public void is_double_on_invalid_config_path() {
        assertFalse(config.isDouble("double_invalid"));
    }

    @Test
    @Override
    public void get_long() {
        assertEquals(Long.MAX_VALUE, config.getLong("long"));
    }

    @Test
    @Override
    public void get_long_on_invalid_config_path() {
        assertEquals(0L, config.getLong("long_invalid"));
    }

    @Test
    @Override
    public void get_long_with_default() {
        assertEquals(Long.MAX_VALUE, config.getLong("long", Long.MIN_VALUE));
    }

    @Test
    @Override
    public void get_long_with_default_on_invalid_config_path() {
        assertEquals(Long.MIN_VALUE, config.getLong("long_invalid", Long.MIN_VALUE));
    }

    @Test
    @Override
    public void is_long() {
        assertTrue(config.isLong("long"));
    }

    @Test
    @Override
    public void is_long_on_invalid_config_path() {
        assertFalse(config.isLong("long_invalid"));
    }

    @Test
    @Override
    public void get_list() {
        assertEquals(Arrays.asList("One", 2, 3.0), config.getList("list"));
    }

    @Test
    @Override
    public void get_list_on_invalid_config_path() {
        assertNull(config.getList("list_invalid"));
    }

    @Test
    @Override
    public void get_list_with_default() {
        assertEquals(Arrays.asList("One", 2, 3.0), config.getList("list", Arrays.asList("Four", 5, 6.0)));
    }

    @Test
    @Override
    public void get_list_with_default_on_invalid_config_path() {
        assertEquals(Arrays.asList("Four", 5, 6.0), config.getList("list_invalid", Arrays.asList("Four", 5, 6.0)));
    }

    @Test
    @Override
    public void is_list() {
        assertTrue(config.isList("list"));
    }

    @Test
    @Override
    public void is_list_on_invalid_config_path() {
        assertFalse(config.isList("list_invalid"));
    }

    @Test
    @Override
    public void get_string_list() {
        assertEquals(Arrays.asList("One", "Two", "Three"), config.getStringList("stringList"));
    }

    @Test
    @Override
    public void get_string_list_on_invalid_config_path() {
        assertEquals(Collections.emptyList(), config.getStringList("stringList_invalid"));
    }

    @Test
    @Override
    public void get_integer_list() {
        assertEquals(Arrays.asList(1, 2, 3), config.getIntegerList("integerList"));
    }

    @Test
    @Override
    public void get_integer_list_on_invalid_config_path() {
        assertEquals(Collections.emptyList(), config.getIntegerList("integerList_invalid"));
    }

    @Test
    @Override
    public void get_boolean_list() {
        assertEquals(Arrays.asList(true, false, true), config.getBooleanList("booleanList"));
    }

    @Test
    @Override
    public void get_boolean_list_on_invalid_config_path() {
        assertEquals(Collections.emptyList(), config.getBooleanList("booleanList_invalid"));
    }

    @Test
    @Override
    public void get_double_list() {
        assertEquals(Arrays.asList(1.1, 2.2, 3.3), config.getDoubleList("doubleList"));
    }

    @Test
    @Override
    public void get_double_list_on_invalid_config_path() {
        assertEquals(Collections.emptyList(), config.getDoubleList("doubleList_invalid"));
    }

    @Test
    @Override
    public void get_float_list() {
        assertEquals(Arrays.asList(1.1F, 2.2F, 3.3F), config.getFloatList("doubleList"));
    }

    @Test
    @Override
    public void get_float_list_on_invalid_config_path() {
        assertEquals(Collections.emptyList(), config.getFloatList("doubleList_invalid"));
    }

    @Test
    @Override
    public void get_long_list() {
        assertEquals(Arrays.asList(1L, 2L, 3L), config.getLongList("integerList"));
    }

    @Test
    @Override
    public void get_long_list_on_invalid_config_path() {
        assertEquals(Collections.emptyList(), config.getFloatList("integerList_invalid"));
    }

    @Test
    @Override
    public void get_byte_list() {
        assertEquals(Arrays.asList((byte) 1, (byte) 2, (byte) 3), config.getByteList("integerList"));
    }

    @Test
    @Override
    public void get_byte_list_on_invalid_config_path() {
        assertEquals(Collections.emptyList(), config.getByteList("integerList_invalid"));
    }

    @Test
    @Override
    public void get_character_list() {
        assertEquals(Arrays.asList('a', 'b', 'c'), config.getCharacterList("characterList"));
    }

    @Test
    @Override
    public void get_character_list_on_invalid_config_path() {
        assertEquals(Collections.emptyList(), config.getCharacterList("characterList_invalid"));
    }

    @Test
    @Override
    public void get_short_list() {
        assertEquals(Arrays.asList((short) 1, (short) 2, (short) 3), config.getShortList("integerList"));
    }

    @Test
    @Override
    public void get_short_list_on_invalid_config_path() {
        assertEquals(Collections.emptyList(), config.getByteList("integerList_invalid"));
    }

    @Test
    @Override
    public void get_map_list() {
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
    public void get_map_list_on_invalid_config_path() {
        assertEquals(Collections.emptyList(), config.getMapList("mapList_invalid"));
    }

    @Test
    @Override
    public void get_object() {
        assertEquals(new TestObject("Test", 5, 555L), config.getObject("object", TestObject.class));
    }

    @Test
    @Override
    public void get_object_on_invalid_config_path() {
        assertNull(config.getObject("object_invalid", TestObject.class));
    }

    @Test
    @Override
    public void get_object_with_default() {
        assertEquals(new TestObject("Test", 5, 555L),
                config.getObject("object", TestObject.class, new TestObject("Test2", 4, 444L)));
    }

    @Test
    @Override
    public void get_object_with_default_on_invalid_config_path() {
        assertEquals(new TestObject("Test2", 4, 444L),
                config.getObject("object_invalid", TestObject.class, new TestObject("Test2", 4, 444L)));
    }

    @Test
    @Override
    public void get_serializable() {
        assertEquals(new TestObject("Test", 5, 555L), config.getSerializable("object", TestObject.class));
    }

    @Test
    @Override
    public void get_serializable_on_invalid_config_path() {
        assertNull(config.getSerializable("object_invalid", TestObject.class));
    }

    @Test
    @Override
    public void get_serializable_with_default() {
        assertEquals(new TestObject("Test", 5, 555L),
                config.getSerializable("object", TestObject.class, new TestObject("Test2", 4, 444L)));
    }

    @Test
    @Override
    public void get_serializable_with_default_on_invalid_config_path() {
        assertEquals(new TestObject("Test2", 4, 444L),
                config.getSerializable("object_invalid", TestObject.class, new TestObject("Test2", 4, 444L)));
    }

    @Test
    @Override
    public void get_vector() {
        assertEquals(new Vector(1, 2, 3), config.getVector("vector"));
    }

    @Test
    @Override
    public void get_vector_on_invalid_config_path() {
        assertNull(config.getVector("vector_invalid"));
    }

    @Test
    @Override
    public void get_vector_with_default() {
        assertEquals(new Vector(1, 2, 3), config.getVector("vector", new Vector(4, 5, 6)));
    }

    @Test
    @Override
    public void get_vector_with_default_on_invalid_config_path() {
        assertEquals(new Vector(4, 5, 6), config.getVector("vector_invalid", new Vector(4, 5, 6)));
    }

    @Test
    @Override
    public void is_vector() {
        assertTrue(config.isVector("vector"));
    }

    @Test
    @Override
    public void is_vector_on_invalid_config_path() {
        assertFalse(config.isVector("vector_invalid"));
    }

    @Test
    @Override
    public void get_offline_player() {
        final OfflinePlayer player = config.getOfflinePlayer("offlinePlayer");
        assertNotNull(player);
        assertEquals(UUID.fromString("eba17d33-959d-42a7-a4d9-e9aebef5969e"), player.getUniqueId());
    }

    @Test
    @Override
    public void get_offline_player_on_invalid_config_path() {
        assertNull(config.getOfflinePlayer("offlinePlayer_invalid"));
    }

    @Test
    @Override
    public void get_offline_player_with_default() {
        final OfflinePlayer offlinePlayerDefault = ConfigurationBuilderFixture.getMockedOfflinePlayer(UUID.fromString("5179617b-8418-4099-8773-37a4ac587dd8"));
        final OfflinePlayer player = config.getOfflinePlayer("offlinePlayer", offlinePlayerDefault);
        assertNotNull(player);
        assertEquals(UUID.fromString("eba17d33-959d-42a7-a4d9-e9aebef5969e"), player.getUniqueId());
    }

    @Test
    @Override
    public void get_offline_player_with_default_on_invalid_config_path() {
        final OfflinePlayer offlinePlayerDefault = ConfigurationBuilderFixture.getMockedOfflinePlayer(UUID.fromString("5179617b-8418-4099-8773-37a4ac587dd8"));
        final OfflinePlayer player = config.getOfflinePlayer("offlinePlayer_invalid", offlinePlayerDefault);
        assertNotNull(player);
        assertEquals(offlinePlayerDefault.getUniqueId(), player.getUniqueId());
    }

    @Test
    @Override
    public void is_offline_player() {
        assertTrue(config.isOfflinePlayer("offlinePlayer"));
    }

    @Test
    @Override
    public void is_offline_player_on_invalid_config_path() {
        assertFalse(config.isOfflinePlayer("offlinePlayer_invalid"));
    }

    @Test
    @Override
    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    public void get_item_stack() {
        final ItemStack configItem = config.getItemStack("item");
        assertNotNull(configItem);
        assertEquals(Material.BONE, configItem.getType());
        assertEquals(42, configItem.getAmount());
    }

    @Test
    @Override
    public void get_item_stack_on_invalid_config_path() {
        assertNull(config.getItemStack("item_invalid"));
    }

    @Test
    @Override
    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    public void get_item_stack_with_default() {
        final ItemStack itemDefault = mock(ItemStack.class);
        final ItemStack configItem = config.getItemStack("item", itemDefault);
        assertNotEquals(itemDefault, configItem);
        assertEquals(Material.BONE, configItem.getType());
        assertEquals(42, configItem.getAmount());
    }

    @Test
    @Override
    public void get_item_stack_with_default_on_invalid_config_path() {
        final ItemStack itemDefault = new ItemStack(Material.BONE_MEAL, 12);
        assertEquals(itemDefault, config.getItemStack("item_invalid", itemDefault));
    }

    @Test
    @Override
    public void is_item_stack() {
        assertTrue(config.isItemStack("item"));
    }

    @Test
    @Override
    public void is_item_stack_on_invalid_config_path() {
        assertFalse(config.isItemStack("item_invalid"));
    }

    @Test
    @Override
    public void get_color() {
        assertEquals(Color.RED, config.getColor("color"));
    }

    @Test
    @Override
    public void get_color_on_invalid_config_path() {
        assertNull(config.getColor("color_invalid"));
    }

    @Test
    @Override
    public void get_color_with_default() {
        assertEquals(Color.RED, config.getColor("color", Color.GREEN));
    }

    @Test
    @Override
    public void get_color_with_default_on_invalid_config_path() {
        assertEquals(Color.GREEN, config.getColor("color_invalid", Color.GREEN));
    }

    @Test
    @Override
    public void is_color() {
        assertTrue(config.isColor("color"));
    }

    @Test
    @Override
    public void is_color_on_invalid_config_path() {
        assertFalse(config.isColor("color_invalid"));
    }

    @Test
    @Override
    public void get_location() {
        final Location location = new Location(WORLD, 1, 2, 3, 4, 5);
        assertEquals(location, config.getLocation("location"));
    }

    @Test
    @Override
    public void get_location_on_invalid_config_path() {
        assertNull(config.getLocation("location_invalid"));
    }

    @Test
    @Override
    public void get_location_with_default() {
        final Location location = new Location(WORLD, 1, 2, 3, 4, 5);
        final Location locationDefault = new Location(worldInvalid, 1, 2, 3, 4, 5);
        assertEquals(location, config.getLocation("location", locationDefault));
    }

    @Test
    @Override
    public void get_location_with_default_on_invalid_config_path() {
        final Location locationDefault = new Location(worldInvalid, 1, 2, 3, 4, 5);
        assertEquals(locationDefault, config.getLocation("location_invalid", locationDefault));
    }

    @Test
    @Override
    public void is_location() {
        assertTrue(config.isLocation("location"));
    }

    @Test
    @Override
    public void is_location_on_invalid_config_path() {
        assertFalse(config.isLocation("location_invalid"));
    }

    @Test
    @Override
    public void get_configuration_section() {
        final ConfigurationSection section = config.getConfigurationSection("section");
        assertNotNull(section);
        assertEquals("value", section.getString("key"));
    }

    @Test
    @Override
    public void get_configuration_section_on_invalid_config_path() {
        assertNull(config.getConfigurationSection("section_invalid"));
    }

    @Test
    @Override
    public void is_configuration_section() {
        assertTrue(config.isConfigurationSection("section"));
    }

    @Test
    @Override
    public void is_configuration_section_on_invalid_config_path() {
        assertFalse(config.isConfigurationSection("section_invalid"));
    }

    @Test
    @Override
    public void get_default_section() {
        final ConfigurationSection defaultSection = config.getDefaultSection();
        assertNotNull(defaultSection);
        assertEquals("value", defaultSection.getString("default.key"));
    }

    @Test
    @Override
    public void get_default_section_on_invalid_config_path() {
        assertNull(config.getConfigurationSection("default_invalid"));
    }

    @Test
    @Override
    public void add_default() {
        config.addDefault("default.add", "value");
        assertEquals("value", config.getString("default.add"));
    }

    @Test
    @Override
    public void add_default_on_existing_config_path() {
        config.addDefault("default.override", "first");
        config.addDefault("default.override", "second");
        assertEquals("second", config.getString("default.override"));
    }

    @Test
    @Override
    public void set_comments() {
        final List<String> comments = new ArrayList<>();
        comments.add("Test Comment");
        config.setComments("existingSet", comments);
        assertEquals(comments, config.getComments("existingSet"));
    }

    @Test
    @Override
    public void set_comments_on_invalid() {
        final List<String> comments = new ArrayList<>();
        comments.add("Test Comment");
        config.setComments("existingSet_invalid", comments);
        assertEquals(new ArrayList<>(), config.getComments("existingSet_invalid"));
    }

    @Test
    @Override
    public void set_inline_comments() {
        final List<String> comments = new ArrayList<>();
        comments.add("Test Inline Comment");
        config.setInlineComments("existingSet", comments);
        assertEquals(comments, config.getInlineComments("existingSet"));
    }

    @Test
    @Override
    public void set_inline_comments_on_invalid() {
        final List<String> comments = new ArrayList<>();
        comments.add("Test Comment");
        config.setInlineComments("existingSet_invalid", comments);
        assertEquals(new ArrayList<>(), config.getInlineComments("existingSet_invalid"));
    }

    @Test
    @Override
    public void get_comments() {
        final List<String> comments = new ArrayList<>();
        comments.add("Test Comment");
        assertEquals(comments, config.getComments("get"));
    }

    @Test
    @Override
    public void get_comments_on_invalid() {
        assertEquals(new ArrayList<>(), config.getComments("get_invalid"));
    }

    @Test
    @Override
    public void get_inline_comments() {
        final List<String> comments = new ArrayList<>();
        comments.add("Test Inline Comment");
        assertEquals(comments, config.getInlineComments("get"));
    }

    @Test
    @Override
    public void get_inline_comments_on_invalid() {
        assertEquals(new ArrayList<>(), config.getInlineComments("get_invalid"));
    }

    @Test
    @Override
    public void to_string() {
        final Pattern pattern = Pattern.compile("\\w+"
                + Pattern.quote("[path='', root='") + "\\w+"
                + Pattern.quote("']"));
        final String sectionString = config.toString();
        final Matcher matcher = pattern.matcher(sectionString);
        assertTrue(matcher.matches(), "Didn't match regex '%s'\nActual string '%s'".formatted(pattern, sectionString));
    }
}
