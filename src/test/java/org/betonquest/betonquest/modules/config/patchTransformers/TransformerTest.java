package org.betonquest.betonquest.modules.config.patchTransformers;

import org.betonquest.betonquest.modules.config.Patcher;
import org.betonquest.betonquest.modules.logger.util.BetonQuestLoggerService;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(BetonQuestLoggerService.class)
public class TransformerTest {

    private static final File CONFIG_FILE = new File("src/test/resources/modules.config/config.yml");
    private static final YamlConfiguration CONFIG = new YamlConfiguration();

    @BeforeEach
    void resetConfig() throws IOException, InvalidConfigurationException {
        CONFIG.load(CONFIG_FILE);
    }

    @Test
    void testValueRename() throws InvalidConfigurationException {
        final String patch = """
                2.0.0.1:
                  - type: VALUE_RENAME
                    key: section.testKey
                    oldValueRegex: test
                    newValue: newTest
                """;
        final String serializedConfig = getSerializedPatchedConfig(patch);

        CONFIG.set("section.testKey", "newTest");
        assertEquals(CONFIG.saveToString(), serializedConfig);
    }

    @Test
    void testListAddLast() throws InvalidConfigurationException {
        final String patch = """
                2.0.0.1:
                    - type: LIST_ENTRY_ADD
                      key: section.myList
                      entry: newEntry
                      position: LAST
                """;
        final String serializedConfig = getSerializedPatchedConfig(patch);

        final List<String> list = CONFIG.getStringList("section.myList");
        list.add("newEntry");
        CONFIG.set("section.myList", list);
        assertEquals(CONFIG.saveToString(), serializedConfig);
    }

    @Test
    void testListAddFirst() throws InvalidConfigurationException {
        final String patch = """
                2.0.0.1:
                    - type: LIST_ENTRY_ADD
                      key: section.myList
                      entry: newEntry
                      position: FIRST
                """;
        final String serializedConfig = getSerializedPatchedConfig(patch);

        final List<String> list = CONFIG.getStringList("section.myList");
        final List<String> newList = new ArrayList<>();
        newList.add("newEntry");
        newList.addAll(list);
        CONFIG.set("section.myList", newList);
        assertEquals(CONFIG.saveToString(), serializedConfig);
    }

    @Test
    void testListEntryRename() throws InvalidConfigurationException {
        final String patch = """
                2.0.0.1:
                  - type: LIST_ENTRY_RENAME
                    key: section.myList
                    oldEntryRegex: currentEntry
                    newEntry: newEntry
                """;
        final String serializedConfig = getSerializedPatchedConfig(patch);

        final List<String> list = CONFIG.getStringList("section.myList");
        final int index = list.indexOf("currentEntry");
        list.set(index, "newEntry");
        CONFIG.set("section.myList", list);

        assertEquals(CONFIG.saveToString(), serializedConfig);
    }

    @Test
    void testListRemove() throws InvalidConfigurationException {
        final String patch = """
                  2.0.0.1:
                    - type: LIST_ENTRY_REMOVE
                      key: section.myList
                      entry: removedEntry
                """;
        final String serializedConfig = getSerializedPatchedConfig(patch);

        final List<?> list = CONFIG.getList("section.myList");
        list.remove("removedEntry");
        CONFIG.set("section.myList", list);

        assertEquals(CONFIG.saveToString(), serializedConfig);
    }

    @Test
    void testRemove() throws InvalidConfigurationException {
        final String patch = """
                2.0.0.1:
                    - type: REMOVE
                      key: section.myList
                """;
        final String serializedConfig = getSerializedPatchedConfig(patch);

        CONFIG.set("section.myList", null);

        assertEquals(CONFIG.saveToString(), serializedConfig);
    }

    @Test
    void testKeyRename() throws InvalidConfigurationException {
        final String patch = """
                2.0.0.1:
                    - type: KEY_RENAME
                      oldKey: section.test
                      newKey: section.testNew
                """;
        final String serializedConfig = getSerializedPatchedConfig(patch);


        final String value = CONFIG.getString("section.test");
        CONFIG.set("section.test", null);
        CONFIG.set("section.testNew", value);

        assertEquals(CONFIG.saveToString(), serializedConfig);
    }

    @Test
    void testSet() throws InvalidConfigurationException {
        final String patch = """
                  2.0.0.1:
                    - type: SET
                      key: journalLock
                      value: true
                """;
        final String serializedConfig = getSerializedPatchedConfig(patch);

        CONFIG.set("journalLock", "true");

        assertEquals(CONFIG.saveToString(), serializedConfig);
    }

    private String getSerializedPatchedConfig(final String patch) throws InvalidConfigurationException {
        final YamlConfiguration patchConfig = new YamlConfiguration();
        patchConfig.loadFromString(patch);

        //Config must be "cloned", otherwise the tests would compare the same object
        final YamlConfiguration questConfig = new YamlConfiguration();
        questConfig.loadFromString(CONFIG.saveToString());

        final Patcher patcher = new Patcher(questConfig, patchConfig);
        patcher.patch();
        return questConfig.saveToString();
    }
}
