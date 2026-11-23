package org.betonquest.betonquest.config.patcher.migration;

import org.betonquest.betonquest.api.config.section.multi.MultiConfiguration;
import org.betonquest.betonquest.config.quest.QuestFixture;
import org.betonquest.betonquest.lib.config.patcher.migration.QuestMigration;
import org.betonquest.betonquest.lib.config.quest.Quest;
import org.bukkit.configuration.InvalidConfigurationException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * Generic tests for Migration types.
 */
class QuestMigrationTest extends QuestFixture {

    @Test
    void flat() throws IOException, InvalidConfigurationException {
        original.loadFromString("""
                # Test Comment 1
                old:
                  # Test Comment 2
                  type: beton
                """);
        final Quest quest = setupQuest("other.yml");
        new RenameSection("old", "new").migrate(quest);
        quest.saveAll();
        expected.loadFromString("""
                # Test Comment 1
                new:
                  # Test Comment 2
                  type: beton
                """);
        checkAssertion(quest, "other.yml");
    }

    @Test
    void deep() throws IOException, InvalidConfigurationException {
        original.loadFromString("""
                # Test Comment 1
                old:
                  # Test Comment 2
                  avc:
                    # Test Comment 3
                    type: beton
                """);
        final Quest quest = setupQuest("other.yml");
        new RenameSection("old", "new").migrate(quest);
        quest.saveAll();
        expected.loadFromString("""
                # Test Comment 1
                new:
                  # Test Comment 2
                  avc:
                    # Test Comment 3
                    type: beton
                """);
        checkAssertion(quest, "other.yml");
    }

    @Test
    void nested() throws IOException, InvalidConfigurationException {
        original.loadFromString("""
                old:
                  avc:
                    type: beton
                  val: eins
                  really.really.deep: yes
                """);
        final Quest quest = setupQuest("other.yml");
        new RenameSection("old", "new").migrate(quest);
        quest.saveAll();
        expected.loadFromString("""
                new:
                  avc:
                    type: beton
                  val: eins
                  really.really.deep: yes
                """);
        checkAssertion(quest, "other.yml");
    }

    @Test
    void migrate_sub_sections_for_singe_line_instructions() throws InvalidConfigurationException, IOException {
        original.loadFromString("""
                bar:
                  foo1: oldValue Bob 0;0;0;world
                  foo:
                    two: oldValue Bob 0;0;0;world
                    three: different Bob
                """);
        expected.loadFromString("""
                bar:
                  foo1: newValue Bob 0;0;0;world
                  foo:
                    two: newValue Bob 0;0;0;world
                    three: different Alex
                """);

        final Quest quest = setupQuest();
        new QuestMigration() {
            @Override
            public void migrate(final Quest quest) {
                final MultiConfiguration config = quest.getQuestConfig();
                replaceStartValueInSection(config, "bar", "oldValue", "newValue");
                replaceValueInSection(config, "bar", "different", "Bob", "Alex");
            }
        }.migrate(quest);
        quest.saveAll();

        checkAssertion(quest, "package.yml");
    }

    @Test
    void expand_to_sub_section_single_value() throws IOException, InvalidConfigurationException {
        original.loadFromString("""
                actions:
                  myAction: "custom val1"
                  other: "something else"
                """);
        expected.loadFromString("""
                actions:
                  myAction:
                    1: "val1"
                  other: "something else"
                """);

        final Quest quest = setupQuest();
        new ExpandAction("custom ").migrate(quest);
        quest.saveAll();

        checkAssertion(quest, "package.yml");
    }

    @Test
    void expand_to_sub_section_multiple_values() throws IOException, InvalidConfigurationException {
        original.loadFromString("""
                actions:
                  myAction: "custom val1 val2 val3"
                """);
        expected.loadFromString("""
                actions:
                  myAction:
                    1: "val1"
                    2: "val2"
                    3: "val3"
                """);

        final Quest quest = setupQuest();
        new ExpandAction("custom ").migrate(quest);
        quest.saveAll();

        checkAssertion(quest, "package.yml");
    }

    @Test
    void expand_to_sub_section_no_matching_prefix() throws IOException, InvalidConfigurationException {
        original.loadFromString("""
                actions:
                  myAction: "other val1"
                """);
        expected.loadFromString("""
                actions:
                  myAction: "other val1"
                """);

        final Quest quest = setupQuest();
        new ExpandAction("custom ").migrate(quest);
        quest.saveAll();

        checkAssertion(quest, "package.yml");
    }

    @Test
    void expand_to_sub_section_in_nested_subsection() throws IOException, InvalidConfigurationException {
        original.loadFromString("""
                actions:
                  group:
                    nested: "custom val1 val2"
                    other: "keep me"
                """);
        expected.loadFromString("""
                actions:
                  group:
                    nested:
                      1: "val1"
                      2: "val2"
                    other: "keep me"
                """);

        final Quest quest = setupQuest();
        new ExpandAction("custom ").migrate(quest);
        quest.saveAll();

        checkAssertion(quest, "package.yml");
    }

    @Test
    void expand_to_sub_section_deeply_nested() throws IOException, InvalidConfigurationException {
        original.loadFromString("""
                actions:
                  level1:
                    level2:
                      level3: "custom deep"
                """);
        expected.loadFromString("""
                actions:
                  level1:
                    level2:
                      level3:
                        1: "deep"
                """);

        final Quest quest = setupQuest();
        new ExpandAction("custom ").migrate(quest);
        quest.saveAll();

        checkAssertion(quest, "package.yml");
    }

    @Test
    void expand_to_sub_section_top_level_and_nested_together() throws IOException, InvalidConfigurationException {
        original.loadFromString("""
                actions:
                  top: "custom topVal"
                  group:
                    nested: "custom nestedVal"
                """);
        expected.loadFromString("""
                actions:
                  top:
                    1: "topVal"
                  group:
                    nested:
                      1: "nestedVal"
                """);

        final Quest quest = setupQuest();
        new ExpandAction("custom ").migrate(quest);
        quest.saveAll();

        checkAssertion(quest, "package.yml");
    }

    @Test
    void expand_to_sub_section_preserves_comments() throws IOException, InvalidConfigurationException {
        original.loadFromString("""
                actions:
                  # This is a block comment for myAction
                  myAction: "custom val1 val2" # inline comment
                  other: "keep me"
                """);
        expected.loadFromString("""
                actions:
                  # This is a block comment for myAction
                  myAction: # inline comment
                    1: "val1"
                    2: "val2"
                  other: "keep me"
                """);

        final Quest quest = setupQuest();
        new ExpandAction("custom ").migrate(quest);
        quest.saveAll();

        checkAssertion(quest, "package.yml");
    }

    /**
     * Renames a section.
     *
     * @param oldPath the old path to remove
     * @param newPath the new path to set old values
     */
    private record RenameSection(String oldPath, String newPath) implements QuestMigration {

        @Override
        public void migrate(final Quest quest) throws InvalidConfigurationException {
            renameSection(quest.getQuestConfig(), oldPath, newPath);
        }
    }

    /**
     * Expands values starting with a prefix to numbered subsections, using space-split parsing.
     *
     * @param prefix the prefix to match
     */
    private record ExpandAction(String prefix) implements QuestMigration {

        @Override
        public void migrate(final Quest quest) throws InvalidConfigurationException {
            final Function<String, List<String>> parser = content -> {
                if (content.isBlank()) {
                    return Collections.emptyList();
                }
                final String[] parts = content.split(" ");
                final List<String> result = new java.util.ArrayList<>();
                for (final String part : parts) {
                    if (!part.isEmpty()) {
                        result.add(part);
                    }
                }
                return result;
            };
            expandToSubSection(quest.getQuestConfig(), "actions", prefix, parser);
        }
    }
}
