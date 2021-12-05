package org.betonquest.betonquest.api.bukkit.config.util;

import org.betonquest.betonquest.modules.logger.util.LogValidator;
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
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.mockito.MockedStatic;

import java.io.File;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * This is an abstract class that provides a basic test setup for test with
 * a {@link Configuration} or {@link ConfigurationSection}.
 * It also mocks {@link World}s, {@link ItemStack}s and {@link OfflinePlayer}s.
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
        ConfigurationSerialization.registerClass(AbstractConfigurationSectionTest.TestObject.class);
        ConfigurationSerialization.registerClass(AbstractConfigurationSectionTest.FakeOfflinePlayer.class, "org.bukkit.craftbukkit.CraftOfflinePlayer");

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
}
