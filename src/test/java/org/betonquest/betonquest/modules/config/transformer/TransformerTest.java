package org.betonquest.betonquest.modules.config.transformer;

import org.betonquest.betonquest.modules.config.Patcher;
import org.betonquest.betonquest.modules.logger.util.BetonQuestLoggerService;
import org.betonquest.betonquest.modules.logger.util.LogValidator;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This test tests all config transformers.
 */
@SuppressWarnings({"PMD.AvoidDuplicateLiterals"})
@ExtendWith(BetonQuestLoggerService.class)
class TransformerTest {

    /**
     * The file that contains a demo config for this test.
     */
    private static final File CONFIG_FILE = new File("src/test/resources/modules.config/config.yml");

    /**
     * The demo config that is used for this test.
     */
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
        assertEquals(CONFIG.saveToString(), serializedConfig, "Patch was not applied correctly.");
    }

    @Test
    void testValueRenameErrorMissingKey(final LogValidator validator) throws InvalidConfigurationException {
        final String patch = """
                2.0.0.1:
                  - type: VALUE_RENAME
                    key: section.invalidKey
                    oldValueRegex: test
                    newValue: newTest
                """;
        final String serializedConfig = getSerializedPatchedConfig(patch);
        validateLogging(validator, "VALUE_RENAME", "The key 'section.invalidKey' did not exist, skipping transformation.");
        assertEquals(CONFIG.saveToString(), serializedConfig, "Patch was not applied correctly.");
    }

    @Test
    void testValueRenameErrorNoMatchForValue(final LogValidator validator) throws InvalidConfigurationException {
        final String patch = """
                2.0.0.1:
                  - type: VALUE_RENAME
                    key: section.testKey
                    oldValueRegex: noMatchRegex
                    newValue: newTest
                """;
        final String serializedConfig = getSerializedPatchedConfig(patch);
        validateLogging(validator, "VALUE_RENAME", "Value does not match the given regex, skipping transformation.");
        assertEquals(CONFIG.saveToString(), serializedConfig, "Patch was not applied correctly.");
    }

    @Test
    void testListEntryAddLast() throws InvalidConfigurationException {
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
        assertEquals(CONFIG.saveToString(), serializedConfig, "Patch was not applied correctly.");
    }

    @Test
    void testListEntryAddFirst() throws InvalidConfigurationException {
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
        assertEquals(CONFIG.saveToString(), serializedConfig, "Patch was not applied correctly.");
    }

    @Test
    void testListEntryAddDefault() throws InvalidConfigurationException {
        final String patch = """
                2.0.0.1:
                    - type: LIST_ENTRY_ADD
                      key: section.myList
                      entry: newEntry
                """;
        final String serializedConfig = getSerializedPatchedConfig(patch);

        final List<String> list = CONFIG.getStringList("section.myList");
        list.add("newEntry");
        CONFIG.set("section.myList", list);
        assertEquals(CONFIG.saveToString(), serializedConfig, "Patch was not applied correctly.");
    }

    @Test
    void testListEntryAddMissingKey(final LogValidator validator) throws InvalidConfigurationException {
        final String patch = """
                2.0.0.1:
                    - type: LIST_ENTRY_ADD
                      key: section.invalidKey
                      entry: newEntry
                      position: LAST
                """;
        final String serializedConfig = getSerializedPatchedConfig(patch);

        final List<String> list = CONFIG.getStringList("section.invalidKey");
        list.add("newEntry");
        CONFIG.set("section.invalidKey", list);

        validateLogging(validator, "LIST_ENTRY_ADD", "List 'section.invalidKey' did not exist, so it was created.");
        assertEquals(CONFIG.saveToString(), serializedConfig, "Patch was not applied correctly.");
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

        assertEquals(CONFIG.saveToString(), serializedConfig, "Patch was not applied correctly.");
    }

    @Test
    void testListEntryRenameMissingKey(final LogValidator validator) throws InvalidConfigurationException {
        final String patch = """
                2.0.0.1:
                  - type: LIST_ENTRY_RENAME
                    key: section.invalidKey
                    oldEntryRegex: currentEntry
                    newEntry: newEntry
                """;
        final String serializedConfig = getSerializedPatchedConfig(patch);
        validateLogging(validator, "LIST_ENTRY_RENAME", "List 'section.invalidKey' did not exist, skipping transformation.");
        assertEquals(CONFIG.saveToString(), serializedConfig, "Patch was not applied correctly.");
    }

    @Test
    void testListEntryRenameNoMatchRegex(final LogValidator validator) throws InvalidConfigurationException {
        final String patch = """
                2.0.0.1:
                  - type: LIST_ENTRY_RENAME
                    key: section.myList
                    oldEntryRegex: invalidRegex
                    newEntry: newEntry
                """;
        final String serializedConfig = getSerializedPatchedConfig(patch);
        validateLogging(validator, "LIST_ENTRY_RENAME", "Unable to find an entry for the given regex, skipping transformation.");
        assertEquals(CONFIG.saveToString(), serializedConfig, "Patch was not applied correctly.");
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

        assertEquals(CONFIG.saveToString(), serializedConfig, "Patch was not applied correctly.");
    }

    @Test
    void testListRemove(final LogValidator validator) throws InvalidConfigurationException {
        final String patch = """
                  2.0.0.1:
                    - type: LIST_ENTRY_REMOVE
                      key: section.invalidList
                      entry: removedEntry
                """;
        final String serializedConfig = getSerializedPatchedConfig(patch);

        validateLogging(validator, "LIST_ENTRY_REMOVE", "The list 'section.invalidList' did not exist, skipping transformation.");
        assertEquals(CONFIG.saveToString(), serializedConfig, "Patch was not applied correctly.");
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

        assertEquals(CONFIG.saveToString(), serializedConfig, "Patch was not applied correctly.");
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

        assertEquals(CONFIG.saveToString(), serializedConfig, "Patch was not applied correctly.");
    }

    @Test
    void testKeyRename(final LogValidator validator) throws InvalidConfigurationException {
        final String patch = """
                2.0.0.1:
                    - type: KEY_RENAME
                      oldKey: section.invalid
                      newKey: section.testNew
                """;
        final String serializedConfig = getSerializedPatchedConfig(patch);


        validateLogging(validator, "KEY_RENAME", "Key 'section.invalid' was not set, skipping transformation to 'section.testNew'.");
        assertEquals(CONFIG.saveToString(), serializedConfig, "Patch was not applied correctly.");
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

        assertEquals(CONFIG.saveToString(), serializedConfig, "Patch was not applied correctly.");
    }

    private String getSerializedPatchedConfig(final String patch) throws InvalidConfigurationException {
        final YamlConfiguration patchConfig = new YamlConfiguration();
        patchConfig.loadFromString(patch);

        //Config must be "cloned", otherwise the tests would compare the same object
        final YamlConfiguration questConfig = new YamlConfiguration();
        questConfig.loadFromString(CONFIG.saveToString());

        //New version is automatically set for all tests
        CONFIG.set("configVersion", "2.0.0-CONFIG-1");

        final Patcher patcher = new Patcher(questConfig, patchConfig);
        patcher.patch();
        return questConfig.saveToString();
    }

    private void validateLogging(final LogValidator validator, final String transformerType, final String exceptionMessage) {
        validator.assertLogEntry(Level.INFO, "Applying patches to update to '2.0.0.1'...");
        validator.assertLogEntry(Level.INFO, "Applying patch of type '" + transformerType + "'...");
        validator.assertLogEntry(Level.WARNING, "There has been an issue while applying the patches for '2.0.0.1': " + exceptionMessage);
    }
}
