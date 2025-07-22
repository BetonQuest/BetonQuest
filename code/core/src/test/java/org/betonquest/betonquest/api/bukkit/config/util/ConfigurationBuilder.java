package org.betonquest.betonquest.api.bukkit.config.util;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.Mockito.*;

/**
 * This class is a builder for {@link Configuration}s for testing.
 */
@SuppressWarnings("PMD.TooManyMethods")
public class ConfigurationBuilder {
    /**
     * The {@link Configuration} that is being built.
     */
    private final Configuration config;

    /**
     * Creates a new {@link ConfigurationBuilder}.
     */
    public ConfigurationBuilder() {
        config = new MemoryConfiguration();
    }

    /**
     * Gets a mocked {@link OfflinePlayer} with the given {@link UUID} setup.
     *
     * @param uuid The {@link UUID} of the {@link OfflinePlayer}
     * @return The mocked {@link OfflinePlayer}
     */
    public static OfflinePlayer getMockedOfflinePlayer(final UUID uuid) {
        final OfflinePlayer offlinePlayer = mock(OfflinePlayer.class);
        when(offlinePlayer.getUniqueId()).thenReturn(uuid);
        return offlinePlayer;
    }

    /**
     * Sets up a "ChildSection" for testing.
     *
     * @return The {@link ConfigurationBuilder} for chaining
     */
    public ConfigurationBuilder setupChildSection() {
        config.set("childSection.nestedChildSection.key", "value");
        return this;
    }

    /**
     * Sets up a "Get" for testing.
     *
     * @return The {@link ConfigurationBuilder} for chaining
     */
    public ConfigurationBuilder setupGet() {
        config.set("get", "getValue");
        config.setComments("get", List.of("Test Comment"));
        config.setInlineComments("get", List.of("Test Inline Comment"));
        return this;
    }

    /**
     * Sets up a "ExistingSet" for testing.
     *
     * @return The {@link ConfigurationBuilder} for chaining
     */
    public ConfigurationBuilder setupExistingSet() {
        config.set("existingSet", "setValue");
        return this;
    }

    /**
     * Sets up a "String" for testing.
     *
     * @return The {@link ConfigurationBuilder} for chaining
     */
    public ConfigurationBuilder setupString() {
        config.set("string", "Custom String");
        return this;
    }

    /**
     * Sets up a "Integer" for testing.
     *
     * @return The {@link ConfigurationBuilder} for chaining
     */
    public ConfigurationBuilder setupInteger() {
        config.set("integer", 12_345);
        return this;
    }

    /**
     * Sets up a "Boolean" for testing.
     *
     * @return The {@link ConfigurationBuilder} for chaining
     */
    public ConfigurationBuilder setupBoolean() {
        config.set("boolean", true);
        return this;
    }

    /**
     * Sets up a "Double" for testing.
     *
     * @return The {@link ConfigurationBuilder} for chaining
     */
    public ConfigurationBuilder setupDouble() {
        config.set("double", 123.45);
        return this;
    }

    /**
     * Sets up a "Long" for testing.
     *
     * @return The {@link ConfigurationBuilder} for chaining
     */
    public ConfigurationBuilder setupLong() {
        config.set("long", Long.MAX_VALUE);
        return this;
    }

    /**
     * Sets up a "List" for testing.
     *
     * @return The {@link ConfigurationBuilder} for chaining
     */
    public ConfigurationBuilder setupList() {
        config.set("list", List.of("One", 2, 3.0));
        return this;
    }

    /**
     * Sets up a "StringList" for testing.
     *
     * @return The {@link ConfigurationBuilder} for chaining
     */
    public ConfigurationBuilder setupStringList() {
        config.set("stringList", List.of("One", "Two", "Three"));
        return this;
    }

    /**
     * Sets up a "IntegerList" for testing.
     *
     * @return The {@link ConfigurationBuilder} for chaining
     */
    public ConfigurationBuilder setupIntegerList() {
        config.set("integerList", List.of(1, 2, 3));
        return this;
    }

    /**
     * Sets up a "BooleanList" for testing.
     *
     * @return The {@link ConfigurationBuilder} for chaining
     */
    public ConfigurationBuilder setupBooleanList() {
        config.set("booleanList", List.of(true, false, true));
        return this;
    }

    /**
     * Sets up a "DoubleList" for testing.
     *
     * @return The {@link ConfigurationBuilder} for chaining
     */
    public ConfigurationBuilder setupDoubleList() {
        config.set("doubleList", List.of(1.1, 2.2, 3.3));
        return this;
    }

    /**
     * Sets up a "CharacterList" for testing.
     *
     * @return The {@link ConfigurationBuilder} for chaining
     */
    public ConfigurationBuilder setupCharacterList() {
        config.set("characterList", List.of('a', 'b', 'c'));
        return this;
    }

    /**
     * Sets up a "MapList" for testing.
     *
     * @return The {@link ConfigurationBuilder} for chaining
     */
    public ConfigurationBuilder setupMapList() {
        config.set("mapList", List.of(Map.of("one", 1, "two", 2), Map.of("three", 3, "four", 4)));
        return this;
    }

    /**
     * Sets up an "Object" for testing.
     *
     * @return The {@link ConfigurationBuilder} for chaining
     */
    public ConfigurationBuilder setupObject() {
        config.set("object", new AbstractConfigBaseTest.TestObject("Test", 5, 555));
        return this;
    }

    /**
     * Sets up a "Vector" for testing.
     *
     * @return The {@link ConfigurationBuilder} for chaining
     */
    public ConfigurationBuilder setupVector() {
        config.set("vector", new Vector(1, 2, 3));
        return this;
    }

    /**
     * Sets up a "Color" for testing.
     *
     * @return The {@link ConfigurationBuilder} for chaining
     */
    public ConfigurationBuilder setupColor() {
        config.set("color", Color.fromRGB(255, 0, 0));
        return this;
    }

    /**
     * Sets up a "Section" for testing.
     *
     * @return The {@link ConfigurationBuilder} for chaining
     */
    public ConfigurationBuilder setupSection() {
        config.set("section.key", "value");
        return this;
    }

    /**
     * Sets up a "Location" for testing.
     *
     * @param world The world to use
     * @return The {@link ConfigurationBuilder} for chaining
     */
    public ConfigurationBuilder setupLocation(final World world) {
        config.set("location", new Location(world, 1, 2, 3, 4, 5));
        return this;
    }

    /**
     * Sets up an "Item" for testing.
     *
     * @return The {@link ConfigurationBuilder} for chaining
     */
    public ConfigurationBuilder setupItem() {
        final ItemStack value = spy(new ItemStack(Material.BONE, 42));
        doReturn("ItemStack {type=BONE, amount=42}").when(value).toString();
        config.set("item", value);
        return this;
    }

    /**
     * Sets up a "OfflinePlayer" for testing.
     *
     * @param uuid The UUID of the player
     * @return The {@link ConfigurationBuilder} for chaining
     */
    public ConfigurationBuilder setupOfflinePlayer(final UUID uuid) {
        config.set("offlinePlayer", getMockedOfflinePlayer(uuid));
        return this;
    }

    /**
     * Returns the built {@link Configuration}.
     *
     * @return the built {@link Configuration}
     */
    public Configuration build() {
        return config;
    }
}
