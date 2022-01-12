package org.betonquest.betonquest.api.bukkit.config.custom.multi.complex;

import org.betonquest.betonquest.api.bukkit.config.custom.multi.KeyConflictException;
import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiConfiguration;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is a test for modifications of the {@link MultiConfiguration}.
 */
@SuppressWarnings({"PMD.JUnitAssertionsShouldIncludeMessage", "PMD.AvoidDuplicateLiterals", "PMD.JUnitTestContainsTooManyAsserts"})
class MultiConfigurationModificationTest {
    /**
     * {@link MultiConfiguration} part 1.
     */
    private final Configuration config1;
    /**
     * {@link MultiConfiguration} part 2.
     */
    private final Configuration config2;
    /**
     * {@link MultiConfiguration} part 3.
     */
    private final Configuration config3;
    /**
     * The {@link MultiConfiguration} instance for testing.
     */
    private final MultiConfiguration config;
    /**
     * A {@link FileConfiguration} containing the result of the merged {@link MultiConfigurationModificationTest#config}.
     */
    private final FileConfiguration configAll;

    /**
     * Create the configs.
     */
    public MultiConfigurationModificationTest() {
        super();
        configAll = YamlConfiguration.loadConfiguration(new File("src/test/resources/api/bukkit/multi/modification/configAll.yml"));
        config1 = YamlConfiguration.loadConfiguration(new File("src/test/resources/api/bukkit/multi/modification/config1.yml"));
        config2 = YamlConfiguration.loadConfiguration(new File("src/test/resources/api/bukkit/multi/modification/config2.yml"));
        config3 = YamlConfiguration.loadConfiguration(new File("src/test/resources/api/bukkit/multi/modification/config3.yml"));
        MultiConfiguration multiConfiguration = null;
        try {
            multiConfiguration = new MultiConfiguration(config1, config2, config3);
        } catch (final KeyConflictException e) {
            final Map<ConfigurationSection, String> configs = new HashMap<>();
            configs.put(config1, "config1.yml");
            configs.put(config2, "config2.yml");
            configs.put(config3, "config3.yml");
            fail(e.resolvedMessage(configs), e);
        } catch (final InvalidConfigurationException e) {
            fail(e);
        }
        config = multiConfiguration;
    }

    @Test
    void testMergedResult() {
        final YamlConfiguration yamlConfig = new YamlConfiguration();
        for (final Map.Entry<String, Object> entry : config.getValues(false).entrySet()) {
            yamlConfig.set(entry.getKey(), entry.getValue());
        }

        assertEquals(configAll.saveToString(), yamlConfig.saveToString());
    }

    @Test
    void testModificationToConfigs() {
        config.set("events.custom.path.give.item", "stone");
        assertTrue(config.needSave());
        config.saveConfigs(toSave -> {
            if (toSave == config1) {
                assertEquals("stone", toSave.getString("events.custom.path.give.item"));
            } else {
                fail();
            }
        });
        assertFalse(config.needSave());

        config.set("events.custom.path.cancel.target", "stone");
        config.set("conditions.items.stone.amount", "stone");
        assertTrue(config.needSave());
        config.saveConfigs(toSave -> {
            if (toSave == config2) {
                assertEquals("stone", toSave.getString("events.custom.path.cancel.target"));
            } else if (toSave == config3) {
                assertEquals("stone", toSave.getString("conditions.items.stone.amount"));
            } else {
                fail();
            }
        });
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

        config.saveConfigs(toSave -> {
            if (toSave == newConfig) {
                assertEquals("block", toSave.getString("objectives.block.type"));
                assertEquals("bedrock", toSave.getString("objectives.block.material"));
            } else {
                fail();
            }
        });

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

        config.saveConfigs(toSave -> {
            if (toSave == newConfig) {
                assertEquals("50", toSave.getString("objectives.block.amount"));
                assertEquals("5", toSave.getString("objectives.block.notify"));
            } else {
                fail();
            }
        });

        assertEquals(newConfig, config.getSourceConfigurationSection("objectives.block"));
    }

    @Test
    void testGetSourceConfigurationSection() {
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
