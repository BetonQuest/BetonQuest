package org.betonquest.betonquest.api.bukkit.config.custom.multi;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.MemorySection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is a test for modifications of the {@link MultiSectionConfiguration}.
 */
@Tag("ConfigurationSection")
@SuppressWarnings({"PMD.UnitTestAssertionsShouldIncludeMessage", "PMD.UnitTestContainsTooManyAsserts", "PMD.JUnit5TestShouldBePackagePrivate"})
public class MultiSectionConfigurationModificationTest {
    /**
     * {@link MultiConfiguration} part 1.
     */
    protected Configuration config1;

    /**
     * {@link MultiConfiguration} part 2.
     */
    protected Configuration config2;

    /**
     * {@link MultiConfiguration} part 3.
     */
    protected Configuration config3;

    /**
     * The {@link MultiConfiguration} instance for testing.
     */
    private MultiConfiguration config;

    /**
     * A {@link Configuration} containing the result of the merged
     * {@link MultiSectionConfigurationModificationTest#config}.
     */
    private Configuration configAll;

    /**
     * Creates the {@link MultiSectionConfigurationModificationTest#configAll},
     * {@link MultiSectionConfigurationModificationTest#config1}, {@link MultiSectionConfigurationModificationTest#config2},
     * {@link MultiSectionConfigurationModificationTest#config3} and {@link MultiSectionConfigurationModificationTest#config}
     * by calling the {@link MultiSectionConfigurationModificationTest#getConfig()} method.
     */
    @BeforeEach
    public void setupConfigs() {
        configAll = new MemoryConfiguration();
        setupConfig1(configAll);
        setupConfig2(configAll);
        setupConfig3(configAll);
        config1 = setupConfig1(new MemoryConfiguration());
        config2 = setupConfig2(new MemoryConfiguration());
        config3 = setupConfig3(new MemoryConfiguration());
        config = getConfig();
    }

    private Configuration setupConfig1(final Configuration config) {
        config.set("events.custom.path.give.type", "give");
        config.set("events.custom.path.give.item", "emerald");
        config.set("events.custom.path.give.amount", 5);
        config.set("events.custom.path.take.type", "give");
        config.set("events.custom.path.take.item", "emerald");
        config.set("events.custom.path.take.amount", 5);

        config.set("events.custom.journal.type", "journal");
        config.set("events.custom.journal.action", "add");
        config.set("events.custom.journal.target", "quest_started");

        return config;
    }

    private Configuration setupConfig2(final Configuration config) {
        config.set("events.custom.path.notify.type", "notify");
        config.set("events.custom.path.notify.message", "Example Message");
        config.set("events.custom.path.notify.io", "chat");
        config.set("events.custom.path.cancel.type", "cancel");
        config.set("events.custom.path.cancel.target", "myQuest");

        config.set("conditions.items.emerald.type", "item");
        config.set("conditions.items.emerald.item", "emerald");
        config.set("conditions.items.emerald.amount", 5);

        return config;
    }

    private Configuration setupConfig3(final Configuration config) {
        config.set("events.custom.finished.type", "notify");
        config.set("events.custom.finished.message", "Quest Finished");
        config.set("events.custom.finished.io", "advancement");

        config.set("events.start.type", "notify");
        config.set("events.start.message", "Quest Started");
        config.set("events.start.io", "advancement");

        config.set("conditions.items.stone.type", "item");
        config.set("conditions.items.stone.item", "stone");
        config.set("conditions.items.stone.amount", 3);

        config.set("conditions.item_iron.type", "item");
        config.set("conditions.item_iron.item", "iron");
        config.set("conditions.item_iron.amount", 2);

        return config;
    }

    /**
     * This method creates the {@link MultiConfiguration} instance for testing.
     *
     * @return the {@link MultiConfiguration} instance for testing.
     */
    protected MultiConfiguration getConfig() {
        final Map<ConfigurationSection, String> configs = new HashMap<>();
        configs.put(config1, "config1.yml");
        configs.put(config2, "config2.yml");
        configs.put(config3, "config3.yml");
        try {
            return new MultiSectionConfiguration(new ArrayList<>(configs.keySet()));
        } catch (final KeyConflictException e) {
            fail(e.resolvedMessage(configs), e);
        } catch (final InvalidConfigurationException e) {
            fail(e);
        }
        return null;
    }

    @Test
    void testMergedResult() {
        assertConfigurationEquals(configAll, config);
    }

    private void assertConfigurationEquals(final ConfigurationSection expected, final ConfigurationSection actual) {
        if (expected == null) {
            assertNull(actual);
            return;
        }
        assertNotNull(actual);
        final Set<String> expectedKeys = expected.getKeys(false);
        final Set<String> actualKeys = actual.getKeys(false);
        assertEquals(expectedKeys.size(), actualKeys.size(), "ConfigurationSections contain different amounts of keys.");
        assertTrue(expectedKeys.containsAll(actualKeys), "ConfigurationSections have differing keys.");
        for (final String key : expectedKeys) {
            if (expected.isConfigurationSection(key)) {
                assertTrue(actual.isConfigurationSection(key));
                assertConfigurationEquals(expected.getConfigurationSection(key), actual.getConfigurationSection(key));
            } else {
                assertEquals(expected.get(key), actual.get(key));
            }
        }
    }

    @Test
    void testModificationToConfigs() {
        config.set("events.custom.path.give.item", "stone");
        assertTrue(config.needSave());
        final Set<ConfigurationSection> unsavedConfigs1 = config.getUnsavedConfigs();
        assertEquals(1, unsavedConfigs1.size());
        final ConfigurationSection unsavedConfig1 = unsavedConfigs1.iterator().next();
        assertEquals(config1, unsavedConfig1);
        assertEquals("stone", unsavedConfig1.getString("events.custom.path.give.item"));
        assertTrue(config.markAsSaved(unsavedConfig1));
        assertFalse(config.needSave());

        config.set("events.custom.path.cancel.target", "stone");
        config.set("conditions.items.stone.amount", "stone");
        assertTrue(config.needSave());
        final Set<ConfigurationSection> unsavedConfigs2 = config.getUnsavedConfigs();
        assertEquals(2, unsavedConfigs2.size());
        final Iterator<ConfigurationSection> iterator = unsavedConfigs2.iterator();
        final ConfigurationSection unsavedConfig2 = iterator.next();
        final ConfigurationSection unsavedConfig3 = iterator.next();

        if (unsavedConfig2.equals(config2) && unsavedConfig3.equals(config3)) {
            assertEquals("stone", unsavedConfig2.getString("events.custom.path.cancel.target"));
            assertEquals("stone", unsavedConfig3.getString("conditions.items.stone.amount"));
        } else if (unsavedConfig2.equals(config3) && unsavedConfig3.equals(config2)) {
            assertEquals("stone", unsavedConfig3.getString("events.custom.path.cancel.target"));
            assertEquals("stone", unsavedConfig2.getString("conditions.items.stone.amount"));
        } else {
            fail();
        }

        assertTrue(config.markAsSaved(unsavedConfig2));
        assertTrue(config.markAsSaved(unsavedConfig3));
        assertFalse(config.needSave());
    }

    @Test
    void testUnassociatedKeys() throws InvalidConfigurationException {
        assertFalse(config.needSave());
        config.set("objectives.block.type", "block");
        config.set("objectives.block.material", "bedrock");
        assertTrue(config.needSave());

        final List<String> keys1 = new ArrayList<>();
        keys1.add("objectives.block.type");
        keys1.add("objectives.block.material");
        assertEquals(keys1, config.getUnassociatedKeys());

        final MemorySection newConfig = new MemoryConfiguration();
        config.associateWith(newConfig);
        assertTrue(config.getUnassociatedKeys().isEmpty());
        assertTrue(config.needSave());

        final Set<ConfigurationSection> unsavedConfigs1 = config.getUnsavedConfigs();
        assertEquals(1, unsavedConfigs1.size());
        final ConfigurationSection unsavedConfig1 = unsavedConfigs1.iterator().next();
        assertEquals(newConfig, unsavedConfig1);
        assertEquals("block", unsavedConfig1.getString("objectives.block.type"));
        assertEquals("bedrock", unsavedConfig1.getString("objectives.block.material"));
        assertTrue(config.markAsSaved(unsavedConfig1));

        assertFalse(config.needSave());
        config.set("objectives.block.amount", "50");
        config.set("objectives.block.notify", "5");
        assertTrue(config.needSave());

        final List<String> keys2 = new ArrayList<>();
        keys2.add("objectives.block.amount");
        keys2.add("objectives.block.notify");
        assertEquals(keys2, config.getUnassociatedKeys());

        config.associateWith("objectives.block", newConfig);
        assertTrue(config.getUnassociatedKeys().isEmpty());
        assertTrue(config.needSave());

        final Set<ConfigurationSection> unsavedConfigs2 = config.getUnsavedConfigs();
        assertEquals(1, unsavedConfigs2.size());
        final ConfigurationSection unsavedConfig2 = unsavedConfigs2.iterator().next();
        assertEquals(newConfig, unsavedConfig2);
        assertEquals("50", unsavedConfig2.getString("objectives.block.amount"));
        assertEquals("5", unsavedConfig2.getString("objectives.block.notify"));
        assertTrue(config.markAsSaved(unsavedConfig2));

        assertFalse(config.needSave());
        assertEquals(newConfig, config.getSourceConfigurationSection("objectives.block"));
    }

    @Test
    void testGetSourceConfigurationSection() throws InvalidConfigurationException {
        assertNull(config.getSourceConfigurationSection("events.custom.path.give.slot"));
        config.set("events.custom.path.give.slot", "hand");
        assertThrows(InvalidConfigurationException.class,
                () -> config.getSourceConfigurationSection("events.custom.path.give"),
                "Not all entries are from the same source config");
        config.associateWith("events.custom.path.give.slot", config1);
        assertDoesNotThrow(() -> {
                    final ConfigurationSection config = this.config.getSourceConfigurationSection("events.custom.path.give");
                    assertEquals(config1, config);
                },
                "Not all entries are from the same source config");
    }
}
