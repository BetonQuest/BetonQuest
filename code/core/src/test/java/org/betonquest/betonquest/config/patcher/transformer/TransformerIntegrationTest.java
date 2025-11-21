package org.betonquest.betonquest.config.patcher.transformer;

import org.betonquest.betonquest.api.config.FileConfigAccessor;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.config.patcher.DefaultPatchTransformerRegistry;
import org.betonquest.betonquest.config.patcher.Patcher;
import org.bukkit.configuration.InvalidConfigurationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * This test tests all config transformers.
 */
@SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
class TransformerIntegrationTest extends TransformersFixture {
    private String patch;

    @AfterEach
    void assertAfterPatch(@TempDir final Path tempDir) throws InvalidConfigurationException, IOException {
        final FileConfigAccessor actualConfig = createConfigAccessorFromResources(tempDir, "config.yml", "src/test/resources/config/config.yml");
        final FileConfigAccessor resource = createConfigAccessorFromResources(tempDir, "resource.yml", "src/test/resources/config/resource.yml");

        final FileConfigAccessor patchConfig = createConfigAccessorFromString(tempDir, "config.patch.yml", patch);
        final Patcher patcher = new Patcher(mock(BetonQuestLogger.class), resource, new DefaultPatchTransformerRegistry(), patchConfig);

        patcher.patch(actualConfig);
        config.set("configVersion", "2.0.0-CONFIG-1");
        assertEquals(getStringFromConfigAccessor(config), getStringFromConfigAccessor(actualConfig),
                "Patch was not applied correctly.");
    }

    @Nested
    @SuppressWarnings("PMD.ShortClassName")
    class Type {
        @Test
        void flawless_String_to_Boolean() throws InvalidConfigurationException, IOException {
            patch = """
                      2.0.0.1:
                        - type: TYPE_TRANSFORM
                          key: section.boolean
                          newType: boolean
                    """;

            config.set("section.boolean", true);
        }

        @Test
        void flawless_String_to_Int() throws InvalidConfigurationException, IOException {
            patch = """
                      2.0.0.1:
                        - type: TYPE_TRANSFORM
                          key: section.int
                          newType: integer
                    """;

            config.set("section.int", 2);
        }

        @Test
        void flawless_String_to_Float() throws InvalidConfigurationException, IOException {
            patch = """
                      2.0.0.1:
                        - type: TYPE_TRANSFORM
                          key: section.float
                          newType: float
                    """;

            config.set("section.float", 2.5F);
        }

        @Test
        void flawless_String_to_Double() throws InvalidConfigurationException, IOException {
            patch = """
                      2.0.0.1:
                        - type: TYPE_TRANSFORM
                          key: section.double
                          newType: double
                    """;

            config.set("section.double", 2.123_456_789_123_456_7D);
        }

        @Test
        void flawless_Boolean_to_String() throws InvalidConfigurationException, IOException {
            patch = """
                      2.0.0.1:
                        - type: TYPE_TRANSFORM
                          key: section.boolean
                          newType: string
                    """;

            config.set("section.boolean", "true");
        }

        @Test
        void no_edits_on_invalid() throws InvalidConfigurationException, IOException {
            patch = """
                      2.0.0.1:
                        - type: TYPE_TRANSFORM
                          key: section.invalid
                          newType: boolean
                    """;
        }

        @Test
        void no_edits_on_unsupported_type() throws InvalidConfigurationException, IOException {
            patch = """
                      2.0.0.1:
                        - type: TYPE_TRANSFORM
                          key: section.boolean
                          newType: invalid
                    """;
        }
    }

    @Nested
    @SuppressWarnings("PMD.ShortClassName")
    class Set {
        @Test
        void flawless() throws InvalidConfigurationException, IOException {
            patch = """
                      2.0.0.1:
                        - type: SET
                          key: journalLock
                          value: true
                    """;

            config.set("journalLock", true);
        }
    }

    @Nested
    class KeyRename {
        @Test
        void flawless() throws InvalidConfigurationException, IOException {
            patch = """
                    2.0.0.1:
                        - type: KEY_RENAME
                          oldKey: section.test
                          newKey: section.testNew
                    """;
            final String value = config.getString("section.test");
            config.set("section.test", null);
            config.set("section.testNew", value);
        }

        @Test
        void flawless_on_list() throws InvalidConfigurationException, IOException {
            patch = """
                    2.0.0.1:
                        - type: KEY_RENAME
                          oldKey: section.myList
                          newKey: section.newList
                    """;
            final List<String> value = config.getStringList("section.myList");
            config.set("section.myList", null);
            config.set("section.newList", value);
        }

        @Test
        void no_edits_on_invalid() throws InvalidConfigurationException, IOException {
            patch = """
                    2.0.0.1:
                        - type: KEY_RENAME
                          oldKey: section.invalid
                          newKey: section.testNew
                    """;
        }
    }

    @Nested
    class Remove {
        @Test
        void flawless() throws InvalidConfigurationException, IOException {
            patch = """
                    2.0.0.1:
                        - type: REMOVE
                          key: section.myList
                    """;

            config.set("section.myList", null);
        }

        @Test
        void no_edits_on_invalid() throws InvalidConfigurationException, IOException {
            patch = """
                    2.0.0.1:
                        - type: REMOVE
                          key: section.nonExistent
                    """;
        }
    }

    @Nested
    class ListEntryRemove {
        @Test
        void flawless() throws InvalidConfigurationException, IOException {
            patch = """
                      2.0.0.1:
                        - type: LIST_ENTRY_REMOVE
                          key: section.myList
                          entry: removedEntry
                    """;

            final List<?> list = config.getList("section.myList");
            assertNotNull(list, "List was null.");
            list.remove("removedEntry");
            config.set("section.myList", list);
        }

        @Test
        void no_edits_on_invalid() throws InvalidConfigurationException, IOException {
            patch = """
                      2.0.0.1:
                        - type: LIST_ENTRY_REMOVE
                          key: section.invalidList
                          entry: removedEntry
                    """;
            config.set("section.invalidList", new ArrayList<>());
        }

        @Test
        void no_edits_on_no_matching_entry() throws InvalidConfigurationException, IOException {
            patch = """
                      2.0.0.1:
                        - type: LIST_ENTRY_REMOVE
                          key: section.myList
                          entry: invalidEntry
                    """;
        }
    }

    @Nested
    class ListEntryRename {
        @Test
        void flawless() throws InvalidConfigurationException, IOException {
            patch = """
                    2.0.0.1:
                      - type: LIST_ENTRY_RENAME
                        key: section.myList
                        oldEntryRegex: currentEntry
                        newEntry: newEntry
                    """;

            final List<String> list = config.getStringList("section.myList");
            final int index = list.indexOf("currentEntry");
            list.set(index, "newEntry");
            config.set("section.myList", list);
        }

        @Test
        void no_edits_on_invalid() throws InvalidConfigurationException, IOException {
            patch = """
                    2.0.0.1:
                      - type: LIST_ENTRY_RENAME
                        key: section.invalidKey
                        oldEntryRegex: currentEntry
                        newEntry: newEntry
                    """;
            config.set("section.invalidKey", new ArrayList<>());
        }

        @Test
        void no_edits_on_no_matching_regex() throws InvalidConfigurationException, IOException {
            patch = """
                    2.0.0.1:
                      - type: LIST_ENTRY_RENAME
                        key: section.myList
                        oldEntryRegex: invalidRegex
                        newEntry: newEntry
                    """;
        }
    }

    @Nested
    class ListEntryAdd {
        @Test
        void flawless_add_default() throws InvalidConfigurationException, IOException {
            patch = """
                    2.0.0.1:
                        - type: LIST_ENTRY_ADD
                          key: section.myList
                          entry: newEntry
                    """;

            final List<String> list = config.getStringList("section.myList");
            list.add("newEntry");
            config.set("section.myList", list);
        }

        @Test
        void flawless_add_last() throws InvalidConfigurationException, IOException {
            patch = """
                    2.0.0.1:
                        - type: LIST_ENTRY_ADD
                          key: section.myList
                          entry: newEntry
                          position: LAST
                    """;

            final List<String> list = config.getStringList("section.myList");
            list.add("newEntry");
            config.set("section.myList", list);
        }

        @Test
        void flawless_add_first() throws InvalidConfigurationException, IOException {
            patch = """
                    2.0.0.1:
                        - type: LIST_ENTRY_ADD
                          key: section.myList
                          entry: newEntry
                          position: FIRST
                    """;

            final List<String> list = config.getStringList("section.myList");
            final List<String> newList = new ArrayList<>();
            newList.add("newEntry");
            newList.addAll(list);
            config.set("section.myList", newList);
        }

        @Test
        void flawless_add_invalid_position() throws InvalidConfigurationException, IOException {
            patch = """
                    2.0.0.1:
                        - type: LIST_ENTRY_ADD
                          key: section.myList
                          entry: newEntry
                          position: rubbish
                    """;

            final List<String> list = config.getStringList("section.myList");
            list.add("newEntry");
            config.set("section.myList", list);
        }

        @Test
        void no_edits_on_invalid() throws InvalidConfigurationException, IOException {
            patch = """
                    2.0.0.1:
                        - type: LIST_ENTRY_ADD
                          key: section.invalidKey
                          entry: newEntry
                          position: LAST
                    """;

            final List<String> list = config.getStringList("section.invalidKey");
            list.add("newEntry");
            config.set("section.invalidKey", list);
        }
    }

    @Nested
    class ValueRename {
        @Test
        void flawless() throws InvalidConfigurationException, IOException {
            patch = """
                    2.0.0.1:
                      - type: VALUE_RENAME
                        key: section.testKey
                        oldValueRegex: test
                        newValue: newTest
                    """;

            config.set("section.testKey", "newTest");
        }

        @Test
        void no_edits_on_non_existing() throws InvalidConfigurationException, IOException {
            patch = """
                    2.0.0.1:
                      - type: VALUE_RENAME
                        key: section.invalidKey
                        oldValueRegex: test
                        newValue: newTest
                    """;
        }

        @Test
        void no_edits_on_no_matching_regex() throws InvalidConfigurationException, IOException {
            patch = """
                    2.0.0.1:
                      - type: VALUE_RENAME
                        key: section.testKey
                        oldValueRegex: noMatchRegex
                        newValue: newTest
                    """;
        }
    }
}
