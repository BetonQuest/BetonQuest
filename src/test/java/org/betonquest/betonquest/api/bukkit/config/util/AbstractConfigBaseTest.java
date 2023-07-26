package org.betonquest.betonquest.api.bukkit.config.util;

import org.bukkit.Bukkit;
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
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.mockito.MockedStatic;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * This is an abstract class that provides a basic test setup for tests with
 * a {@link Configuration} or {@link ConfigurationSection}.
 * It also mocks {@link World}s, {@link ItemStack}s and {@link OfflinePlayer}s.
 *
 * @param <T> a {@link ConfigurationSection} or a class implementing {@link ConfigurationSection}
 */
public abstract class AbstractConfigBaseTest<T extends ConfigurationSection> {
    /**
     * The static mock of {@link ItemStack}
     */
    private static MockedStatic<ItemStack> itemStackMockedStatic;

    /**
     * The {@link T} instance for testing
     */
    protected final T config = getConfig();

    /**
     * Empty constructor
     */
    public AbstractConfigBaseTest() {
    }

    /**
     * Mock static things and {@link World}s {@link ItemStack}s and {@link OfflinePlayer}s
     */
    @BeforeAll
    public static void beforeAll() {
        ConfigurationSerialization.registerClass(TestObject.class);
        ConfigurationSerialization.registerClass(FakeOfflinePlayer.class, "org.bukkit.craftbukkit.CraftOfflinePlayer");

        itemStackMockedStatic = mockStatic(ItemStack.class);

        final Server serverMock = mock(Server.class);
        when(serverMock.getLogger()).thenReturn(mock(Logger.class));
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
        when(itemFactory.equals(any(), any())).thenReturn(true);
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

    /**
     * Close the static mocks
     */
    @AfterAll
    public static void afterAll() {
        itemStackMockedStatic.close();
    }

    /**
     * Get the {@link T} that should be tested.
     *
     * @return The {@link T} to be tested
     */
    public abstract T getConfig();

    /**
     * Get the default {@link Configuration} for test.
     *
     * @return The {@link Configuration}
     */
    public final Configuration getDefaultConfig() {
        final Configuration config = YamlConfiguration.loadConfiguration(new File("src/test/resources/api/bukkit/config.yml"));
        final Configuration defaultSection = new MemoryConfiguration();
        defaultSection.set("default.key", "value");
        config.setDefaults(defaultSection);
        return config;
    }

    /**
     * A fake {@link OfflinePlayer} that add a method for deserialization.
     */
    public interface FakeOfflinePlayer extends OfflinePlayer {
        /**
         * Method to deserialize a {@link OfflinePlayer}.
         * This will call the {@link Bukkit#getOfflinePlayer(UUID)} method.
         * Therefore, this is only for the method {@link ConfigurationSerialization#registerClass(Class, String)}.
         *
         * @param args The map of arguments
         * @return The returned {@link OfflinePlayer} from {@link Bukkit#getOfflinePlayer(UUID)}
         */
        @SuppressWarnings("unused")
        static OfflinePlayer deserialize(final Map<String, Object> args) {
            return Bukkit.getOfflinePlayer(UUID.fromString((String) args.get("UUID")));
        }
    }

    /**
     * This is a {@link TestObject} for the related {@link ConfigurationSection} methods.
     *
     * @param name   The pseudo name
     * @param amount The pseudo amount
     * @param sum    The pseudo sum
     */
    public record TestObject(String name, int amount, long sum) implements ConfigurationSerializable {
        /**
         * Method to deserialize a {@link TestObject}.
         *
         * @param args The map of arguments
         * @return The created {@link TestObject}
         */
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
    }
}
