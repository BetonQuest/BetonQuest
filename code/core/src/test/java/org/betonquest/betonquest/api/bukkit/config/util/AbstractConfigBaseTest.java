package org.betonquest.betonquest.api.bukkit.config.util;

import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

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
     * The mocked {@link World} instance for testing
     */
    protected final World world = mock(World.class, "ValidWorld");

    /**
     * The mocked invalid {@link World} instance for testing
     */
    protected final World worldInvalid = mock(World.class, "InvalidWorld");

    /**
     * The {@link T} instance for testing
     */
    protected final T config;

    /**
     * Empty constructor.
     */
    @SuppressWarnings("PMD.ConstructorCallsOverridableMethod")
    public AbstractConfigBaseTest() {
        try {
            config = getConfig();
        } catch (final InvalidConfigurationException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Get the {@link T} that should be tested.
     *
     * @return The {@link T} to be tested
     * @throws InvalidConfigurationException if the {@link Configuration} is invalid
     */
    public abstract T getConfig() throws InvalidConfigurationException;

    /**
     * Get the default {@link Configuration} for test.
     *
     * @return The {@link Configuration}
     */
    public final Configuration getDefaultConfig() {
        final Configuration config = setupConfig();
        final Configuration defaultSection = new MemoryConfiguration();
        defaultSection.set("default.key", "value");
        config.setDefaults(defaultSection);
        return config;
    }

    private Configuration setupConfig() {
        return new ConfigurationBuilder()
                .setupChildSection()
                .setupGet()
                .setupExistingSet()
                .setupString()
                .setupInteger()
                .setupBoolean()
                .setupDouble()
                .setupLong()
                .setupList()
                .setupStringList()
                .setupIntegerList()
                .setupBooleanList()
                .setupDoubleList()
                .setupCharacterList()
                .setupMapList()
                .setupObject()
                .setupVector()
                .setupColor()
                .setupSection()
                .setupLocation(world)
                .setupItem()
                .setupOfflinePlayer(UUID.fromString("eba17d33-959d-42a7-a4d9-e9aebef5969e"))
                .build();
    }

    /**
     * Set up the original configuration.
     *
     * @return The original configuration.
     */
    protected Configuration setupOriginal() {
        return new ConfigurationBuilder()
                .setupChildSection()
                .setupString()
                .setupDouble()
                .setupStringList()
                .setupDoubleList()
                .setupObject()
                .setupSection()
                .setupOfflinePlayer(UUID.fromString("eba17d33-959d-42a7-a4d9-e9aebef5969e"))
                .setupExistingSet()
                .build();
    }

    /**
     * Set up the fallback configuration.
     *
     * @return The fallback configuration.
     */
    protected Configuration setupFallback() {
        return new ConfigurationBuilder()
                .setupGet()
                .setupInteger()
                .setupLong()
                .setupIntegerList()
                .setupCharacterList()
                .setupVector()
                .setupLocation(world)
                .setupBoolean()
                .setupList()
                .setupBooleanList()
                .setupMapList()
                .setupColor()
                .setupItem()
                .build();
    }

    /**
     * Set up a part of the multi configuration.
     *
     * @return The part of the multi configuration.
     */
    protected ConfigurationSection setupMultiConfig1() {
        return new ConfigurationBuilder()
                .setupChildSection()
                .setupString()
                .setupDouble()
                .setupStringList()
                .setupDoubleList()
                .setupObject()
                .setupSection()
                .setupOfflinePlayer(UUID.fromString("eba17d33-959d-42a7-a4d9-e9aebef5969e"))
                .build();
    }

    /**
     * Set up a part of the multi configuration.
     *
     * @return The part of the multi configuration.
     */
    protected ConfigurationSection setupMultiConfig2() {
        return new ConfigurationBuilder()
                .setupGet()
                .setupInteger()
                .setupLong()
                .setupIntegerList()
                .setupCharacterList()
                .setupVector()
                .setupLocation(world)
                .build();
    }

    /**
     * Set up a part of the multi configuration.
     *
     * @return The part of the multi configuration.
     */
    protected ConfigurationSection setupMultiConfig3() {
        return new ConfigurationBuilder()
                .setupExistingSet()
                .setupBoolean()
                .setupList()
                .setupBooleanList()
                .setupMapList()
                .setupColor()
                .setupItem()
                .build();
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
        @SuppressWarnings("unused")
        public static TestObject deserialize(final Map<String, Object> args) {
            return new TestObject((String) args.get("name"), (int) args.get("amount"), (int) args.get("sum"));
        }

        @Override
        public Map<String, Object> serialize() {
            final Map<String, Object> map = new LinkedHashMap<>();
            map.put("name", name);
            map.put("amount", amount);
            map.put("sum", sum);
            return map;
        }
    }
}
