package org.betonquest.betonquest.api.bukkit.config.util;

import org.betonquest.betonquest.modules.logger.util.LogValidator;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.UnsafeValues;
import org.bukkit.World;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AbstractConfigurationSectionTest implements ConfigurationSectionTestInterface {
    private static MockedStatic<ItemStack> itemStackMockedStatic;
    private ConfigurationSection config;

    @BeforeAll
    public static void beforeAll() {
        ConfigurationSerialization.registerClass(TestObject.class);
        ConfigurationSerialization.registerClass(FakeOfflinePlayer.class, "org.bukkit.craftbukkit.CraftOfflinePlayer");

        itemStackMockedStatic = mockStatic(ItemStack.class);

        final Server serverMock = mock(Server.class);
        when(serverMock.getLogger()).thenReturn(LogValidator.getSilentLogger());
        Bukkit.setServer(serverMock);

        mockWorlds(serverMock);
        mockItems(serverMock);
        mockOfflinePlayer(serverMock);
    }

    private static void mockWorlds(final Server serverMock) {
        final World world = mock(World.class);
        final World worldInvalid = mock(World.class);

        when(serverMock.getWorld("Test")).thenReturn(world);
        when(serverMock.getWorld("TestInvalid")).thenReturn(worldInvalid);
    }

    @SuppressWarnings("deprecation")
    private static void mockItems(final Server serverMock) {
        final UnsafeValues values = mock(UnsafeValues.class);
        when(values.getMaterial(eq("BONE"), anyInt())).thenReturn(Material.BONE);
        when(serverMock.getUnsafe()).thenReturn(values);
        final ItemFactory itemFactory = mock(ItemFactory.class);
        when(itemFactory.ensureServerConversions(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
        when(itemFactory.equals(any(),any())).thenReturn(true);
        when(serverMock.getItemFactory()).thenReturn(itemFactory);

        itemStackMockedStatic.when(() -> ItemStack.deserialize(anyMap())).thenReturn(new ItemStack(Material.BONE, 42));
    }

    private static void mockOfflinePlayer(final Server serverMock) {
        when(serverMock.getOfflinePlayer(any(UUID.class))).thenAnswer(invocationOnMock -> {
           final OfflinePlayer offlinePlayer = mock(OfflinePlayer.class);
           when(offlinePlayer.getUniqueId()).thenReturn(invocationOnMock.getArgument(0));
           return offlinePlayer;
        });
    }

    @AfterAll
    public static void afterAll() {
        itemStackMockedStatic.close();
    }

    @BeforeEach
    @SuppressWarnings("unused")
    private void beforeEach() {
        config = getConfig();
    }

    public ConfigurationSection getConfig() {
        final Configuration config = YamlConfiguration.loadConfiguration(new File("src/test/resources/api/bukkit/config.yml"));
        final Configuration defaultSection = new MemoryConfiguration();
        defaultSection.set("default.key", "value");
        config.setDefaults(defaultSection);
        return config;
    }

    @Test
    @Override
    public void testGetKeysDeepFalse() {
        final ConfigurationSection section = config.getConfigurationSection("childSection");
        assertNotNull(section);
        assertEquals(new HashSet<>(Collections.singletonList("nestedChildSection")), section.getKeys(false));
    }

    @Test
    @Override
    public void testGetKeysDeepTrue() {
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
    public void testGetCurrentPath() {
        final ConfigurationSection nestedChild = config.getConfigurationSection("childSection.nestedChildSection");
        assertNotNull(nestedChild);
        assertEquals("childSection.nestedChildSection", nestedChild.getCurrentPath());
    }

    @Test
    @Override
    public void testGetName() {
        final ConfigurationSection nestedChild = config.getConfigurationSection("childSection.nestedChildSection");
        assertNotNull(nestedChild);
        assertEquals("nestedChildSection", nestedChild.getName());
    }

    @Test
    @Override
    public void testGetRoot() {
        assertNotNull(config);
        final ConfigurationSection nestedChild = config.getConfigurationSection("childSection.nestedChildSection");
        assertNotNull(nestedChild);
        final ConfigurationSection nestedChildRoot = nestedChild.getRoot();
        assertNotNull(nestedChildRoot);
        assertEquals(config.getValues(true), nestedChildRoot.getValues(true));
    }

    @Test
    @Override
    public void testGetParent() {
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
    @SuppressWarnings("serial")
    public void testCreateSectionWithValues() {
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
        final ConfigurationSection section = config.createSection("createdSectionWithValuesExist", new HashMap<String, Object>() {{
            put("one", 1);
            put("two", 2);
        }});
        assertEquals(1, config.getInt("createdSectionWithValuesExist.one"));
        assertEquals(2, config.getInt("createdSectionWithValuesExist.two"));
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
        assertEquals(12345, config.getInt("integer"));
    }

    @Test
    @Override
    public void testGetIntOnInvalidConfigPath() {
        assertEquals(0, config.getInt("integer_invalid"));
    }

    @Test
    @Override
    public void testGetIntWithDefault() {
        assertEquals(12345, config.getInt("integer", 54321));
    }

    @Test
    @Override
    public void testGetIntWithDefaultOnInvalidConfigPath() {
        assertEquals(54321, config.getInt("integer_invalid", 54321));
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
    @SuppressWarnings("serial")
    public void testGetMapList() {
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
        final  OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString("eba17d33-959d-42a7-a4d9-e9aebef5969e"));
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
    public void testAddDefault() {
        config.addDefault("default.add", "value");
        assertEquals("value", config.getString("default.add"));
    }

    @Test
    @Override
    public void testAddDefaultOnExistingConfigPath() {
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

    public interface FakeOfflinePlayer extends OfflinePlayer {
        @SuppressWarnings("unused")
        static OfflinePlayer deserialize(final Map<String, Object> args) {
            return Bukkit.getOfflinePlayer(UUID.fromString((String) args.get("UUID")));
        }
    }
}
