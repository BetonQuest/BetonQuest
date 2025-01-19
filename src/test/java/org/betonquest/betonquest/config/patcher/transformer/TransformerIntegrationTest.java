package org.betonquest.betonquest.config.patcher.transformer;

import org.betonquest.betonquest.api.config.patcher.PatchTransformerRegisterer;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.config.Patcher;
import org.betonquest.betonquest.config.patcher.DefaultPatchTransformerRegisterer;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * This test tests all config transformers.
 */
class TransformerIntegrationTest extends TransformersFixture {

    /**
     * Anonymous {@link PatchTransformerRegisterer} for testing.
     */
    public final PatchTransformerRegisterer registerer = new DefaultPatchTransformerRegisterer() {
    };

    private void assertAfterPatch(final String patch) throws InvalidConfigurationException, IOException {
        final YamlConfiguration patchConfig = new YamlConfiguration();
        patchConfig.loadFromString(patch);

        final YamlConfiguration questConfig = new YamlConfiguration();
        questConfig.load(CONFIG_FILE);

        CONFIG.set("configVersion", "2.0.0-CONFIG-1");

        final Patcher patcher = new Patcher(mock(BetonQuestLogger.class), questConfig, patchConfig);
        registerer.registerTransformers(patcher);
        patcher.patch();

        assertEquals(CONFIG.saveToString(), questConfig.saveToString(), "Patch was not applied correctly.");
    }

    @Nested
    @SuppressWarnings("PMD.ShortClassName")
    class Type {
        @Test
        void flawless_String_to_Boolean() throws InvalidConfigurationException, IOException {
            final String patch = """
                      2.0.0.1:
                        - type: TYPE_TRANSFORM
                          key: section.boolean
                          newType: boolean
                    """;

            CONFIG.set("section.boolean", true);
            assertAfterPatch(patch);
        }

        @Test
        void flawless_String_to_Int() throws InvalidConfigurationException, IOException {
            final String patch = """
                      2.0.0.1:
                        - type: TYPE_TRANSFORM
                          key: section.int
                          newType: integer
                    """;

            CONFIG.set("section.int", 2);
            assertAfterPatch(patch);
        }

        @Test
        void flawless_String_to_Float() throws InvalidConfigurationException, IOException {
            final String patch = """
                      2.0.0.1:
                        - type: TYPE_TRANSFORM
                          key: section.float
                          newType: float
                    """;

            CONFIG.set("section.float", 2.5F);
            assertAfterPatch(patch);
        }

        @Test
        void flawless_String_to_Double() throws InvalidConfigurationException, IOException {
            final String patch = """
                      2.0.0.1:
                        - type: TYPE_TRANSFORM
                          key: section.double
                          newType: double
                    """;

            CONFIG.set("section.double", 2.123_456_789_123_456_7D);
            assertAfterPatch(patch);
        }

        @Test
        void flawless_Boolean_to_String() throws InvalidConfigurationException, IOException {
            final String patch = """
                      2.0.0.1:
                        - type: TYPE_TRANSFORM
                          key: section.boolean
                          newType: string
                    """;

            CONFIG.set("section.boolean", "true");
            assertAfterPatch(patch);
        }

        @Test
        void no_edits_on_invalid() throws InvalidConfigurationException, IOException {
            final String patch = """
                      2.0.0.1:
                        - type: TYPE_TRANSFORM
                          key: section.invalid
                          newType: boolean
                    """;
            assertAfterPatch(patch);
        }

        @Test
        void no_edits_on_unsupported_type() throws InvalidConfigurationException, IOException {
            final String patch = """
                      2.0.0.1:
                        - type: TYPE_TRANSFORM
                          key: section.boolean
                          newType: invalid
                    """;
            assertAfterPatch(patch);
        }
    }

    @Nested
    @SuppressWarnings("PMD.ShortClassName")
    class Set {
        @Test
        void flawless() throws InvalidConfigurationException, IOException {
            final String patch = """
                      2.0.0.1:
                        - type: SET
                          key: journalLock
                          value: true
                    """;

            CONFIG.set("journalLock", "true");
            assertAfterPatch(patch);
        }
    }

    @Nested
    class KeyRename {
        @Test
        void flawless() throws InvalidConfigurationException, IOException {
            final String patch = """
                    2.0.0.1:
                        - type: KEY_RENAME
                          oldKey: section.test
                          newKey: section.testNew
                    """;
            final String value = CONFIG.getString("section.test");
            CONFIG.set("section.test", null);
            CONFIG.set("section.testNew", value);
            assertAfterPatch(patch);
        }

        @Test
        void flawless_on_list() throws InvalidConfigurationException, IOException {
            final String patch = """
                    2.0.0.1:
                        - type: KEY_RENAME
                          oldKey: section.myList
                          newKey: section.newList
                    """;
            final List<String> value = CONFIG.getStringList("section.myList");
            CONFIG.set("section.myList", null);
            CONFIG.set("section.newList", value);
            assertAfterPatch(patch);
        }

        @Test
        void no_edits_on_invalid() throws InvalidConfigurationException, IOException {
            final String patch = """
                    2.0.0.1:
                        - type: KEY_RENAME
                          oldKey: section.invalid
                          newKey: section.testNew
                    """;
            assertAfterPatch(patch);
        }
    }

    @Nested
    class Remove {
        @Test
        void flawless() throws InvalidConfigurationException, IOException {
            final String patch = """
                    2.0.0.1:
                        - type: REMOVE
                          key: section.myList
                    """;

            CONFIG.set("section.myList", null);
            assertAfterPatch(patch);
        }

        @Test
        void no_edits_on_invalid() throws InvalidConfigurationException, IOException {
            final String patch = """
                    2.0.0.1:
                        - type: REMOVE
                          key: section.nonExistent
                    """;
            assertAfterPatch(patch);
        }
    }

    @Nested
    class ListEntryRemove {
        @Test
        void flawless() throws InvalidConfigurationException, IOException {
            final String patch = """
                      2.0.0.1:
                        - type: LIST_ENTRY_REMOVE
                          key: section.myList
                          entry: removedEntry
                    """;

            final List<?> list = CONFIG.getList("section.myList");
            assertNotNull(list, "List was null.");
            list.remove("removedEntry");
            CONFIG.set("section.myList", list);
            assertAfterPatch(patch);
        }

        @Test
        void no_edits_on_invalid() throws InvalidConfigurationException, IOException {
            final String patch = """
                      2.0.0.1:
                        - type: LIST_ENTRY_REMOVE
                          key: section.invalidList
                          entry: removedEntry
                    """;
            CONFIG.set("section.invalidList", new ArrayList<>());
            assertAfterPatch(patch);
        }

        @Test
        void no_edits_on_no_matching_entry() throws InvalidConfigurationException, IOException {
            final String patch = """
                      2.0.0.1:
                        - type: LIST_ENTRY_REMOVE
                          key: section.myList
                          entry: invalidEntry
                    """;
            assertAfterPatch(patch);
        }
    }

    @Nested
    class ListEntryRename {
        @Test
        void flawless() throws InvalidConfigurationException, IOException {
            final String patch = """
                    2.0.0.1:
                      - type: LIST_ENTRY_RENAME
                        key: section.myList
                        oldEntryRegex: currentEntry
                        newEntry: newEntry
                    """;

            final List<String> list = CONFIG.getStringList("section.myList");
            final int index = list.indexOf("currentEntry");
            list.set(index, "newEntry");
            CONFIG.set("section.myList", list);
            assertAfterPatch(patch);
        }

        @Test
        void no_edits_on_invalid() throws InvalidConfigurationException, IOException {
            final String patch = """
                    2.0.0.1:
                      - type: LIST_ENTRY_RENAME
                        key: section.invalidKey
                        oldEntryRegex: currentEntry
                        newEntry: newEntry
                    """;
            CONFIG.set("section.invalidKey", new ArrayList<>());
            assertAfterPatch(patch);
        }

        @Test
        void no_edits_on_no_matching_regex() throws InvalidConfigurationException, IOException {
            final String patch = """
                    2.0.0.1:
                      - type: LIST_ENTRY_RENAME
                        key: section.myList
                        oldEntryRegex: invalidRegex
                        newEntry: newEntry
                    """;

            assertAfterPatch(patch);
        }
    }

    @Nested
    class ListEntryAdd {
        @Test
        void flawless_add_default() throws InvalidConfigurationException, IOException {
            final String patch = """
                    2.0.0.1:
                        - type: LIST_ENTRY_ADD
                          key: section.myList
                          entry: newEntry
                    """;

            final List<String> list = CONFIG.getStringList("section.myList");
            list.add("newEntry");
            CONFIG.set("section.myList", list);
            assertAfterPatch(patch);
        }

        @Test
        void flawless_add_last() throws InvalidConfigurationException, IOException {
            final String patch = """
                    2.0.0.1:
                        - type: LIST_ENTRY_ADD
                          key: section.myList
                          entry: newEntry
                          position: LAST
                    """;

            final List<String> list = CONFIG.getStringList("section.myList");
            list.add("newEntry");
            CONFIG.set("section.myList", list);
            assertAfterPatch(patch);
        }

        @Test
        void flawless_add_first() throws InvalidConfigurationException, IOException {
            final String patch = """
                    2.0.0.1:
                        - type: LIST_ENTRY_ADD
                          key: section.myList
                          entry: newEntry
                          position: FIRST
                    """;

            final List<String> list = CONFIG.getStringList("section.myList");
            final List<String> newList = new ArrayList<>();
            newList.add("newEntry");
            newList.addAll(list);
            CONFIG.set("section.myList", newList);
            assertAfterPatch(patch);
        }

        @Test
        void flawless_add_invalid_position() throws InvalidConfigurationException, IOException {
            final String patch = """
                    2.0.0.1:
                        - type: LIST_ENTRY_ADD
                          key: section.myList
                          entry: newEntry
                          position: rubbish
                    """;

            final List<String> list = CONFIG.getStringList("section.myList");
            list.add("newEntry");
            CONFIG.set("section.myList", list);
            assertAfterPatch(patch);
        }

        @Test
        void no_edits_on_invalid() throws InvalidConfigurationException, IOException {
            final String patch = """
                    2.0.0.1:
                        - type: LIST_ENTRY_ADD
                          key: section.invalidKey
                          entry: newEntry
                          position: LAST
                    """;

            final List<String> list = CONFIG.getStringList("section.invalidKey");
            list.add("newEntry");
            CONFIG.set("section.invalidKey", list);
            assertAfterPatch(patch);
        }
    }

    @Nested
    class ValueRename {
        @Test
        void flawless() throws InvalidConfigurationException, IOException {
            final String patch = """
                    2.0.0.1:
                      - type: VALUE_RENAME
                        key: section.testKey
                        oldValueRegex: test
                        newValue: newTest
                    """;

            CONFIG.set("section.testKey", "newTest");
            assertAfterPatch(patch);
        }

        @Test
        void no_edits_on_non_existing() throws InvalidConfigurationException, IOException {
            final String patch = """
                    2.0.0.1:
                      - type: VALUE_RENAME
                        key: section.invalidKey
                        oldValueRegex: test
                        newValue: newTest
                    """;

            assertAfterPatch(patch);
        }

        @Test
        void no_edits_on_no_matching_regex() throws InvalidConfigurationException, IOException {
            final String patch = """
                    2.0.0.1:
                      - type: VALUE_RENAME
                        key: section.testKey
                        oldValueRegex: noMatchRegex
                        newValue: newTest
                    """;

            assertAfterPatch(patch);
        }
    }
}
