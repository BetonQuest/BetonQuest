package org.betonquest.betonquest.config.migrator.from2to3;

import org.betonquest.betonquest.config.quest.QuestFixture;
import org.betonquest.betonquest.lib.config.quest.Quest;
import org.bukkit.configuration.InvalidConfigurationException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link RunActionMigrator}.
 */
@SuppressWarnings("PMD.UnitTestAssertionsShouldIncludeMessage")
class RunActionMigratorTest extends QuestFixture {

    @Test
    void splitRunActions_single_action() {
        final RunActionMigrator migrator = new RunActionMigrator();
        final List<String> result = migrator.splitRunActions("^tag add beton");
        assertEquals(1, result.size());
        assertEquals("tag add beton", result.get(0));
    }

    @Test
    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    void splitRunActions_multiple_actions_with_caret() {
        final RunActionMigrator migrator = new RunActionMigrator();
        final List<String> result = migrator.splitRunActions("^tag add beton ^journal add beton ^give emerald:5 ^kill");
        assertEquals(4, result.size());
        assertEquals("tag add beton", result.get(0));
        assertEquals("journal add beton", result.get(1));
        assertEquals("give emerald:5", result.get(2));
        assertEquals("kill", result.get(3));
    }

    @Test
    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    void splitRunActions_first_action_without_caret() {
        final RunActionMigrator migrator = new RunActionMigrator();
        final List<String> result = migrator.splitRunActions("^tag add beton ^give emerald:5");
        assertEquals(2, result.size());
        assertEquals("tag add beton", result.get(0));
        assertEquals("give emerald:5", result.get(1));
    }

    @Test
    void splitRunActions_empty_string() {
        final RunActionMigrator migrator = new RunActionMigrator();
        final List<String> result = migrator.splitRunActions("");
        assertTrue(result.isEmpty());
    }

    @Test
    void migrate_simple_run_action() throws IOException, InvalidConfigurationException {
        original.loadFromString("""
                actions:
                  eliminate: "run ^tag add beton ^give emerald:5 ^kill"
                """);
        expected.loadFromString("""
                actions:
                  eliminate:
                    1: "tag add beton"
                    2: "give emerald:5"
                    3: "kill"
                """);

        final Quest quest = setupQuest();
        new RunActionMigrator().migrate(quest);
        quest.saveAll();

        checkAssertion(quest, "package.yml");
    }

    @Test
    void migrate_run_action_first_without_caret() throws IOException, InvalidConfigurationException {
        original.loadFromString("""
                actions:
                  myAction: "run ^tag add beton ^give emerald:5"
                """);
        expected.loadFromString("""
                actions:
                  myAction:
                    1: "tag add beton"
                    2: "give emerald:5"
                """);

        final Quest quest = setupQuest();
        new RunActionMigrator().migrate(quest);
        quest.saveAll();

        checkAssertion(quest, "package.yml");
    }

    @Test
    void migrate_run_action_with_single_action() throws IOException, InvalidConfigurationException {
        original.loadFromString("""
                actions:
                  single: "run ^tag add beton"
                """);
        expected.loadFromString("""
                actions:
                  single:
                    1: "tag add beton"
                """);

        final Quest quest = setupQuest();
        new RunActionMigrator().migrate(quest);
        quest.saveAll();

        checkAssertion(quest, "package.yml");
    }

    @Test
    void migrate_run_action_preserves_non_run_actions() throws IOException, InvalidConfigurationException {
        original.loadFromString("""
                actions:
                  eliminate: "run ^tag add beton ^give emerald:5"
                  other: "message Hello"
                """);
        expected.loadFromString("""
                actions:
                  eliminate:
                    1: "tag add beton"
                    2: "give emerald:5"
                  other: "message Hello"
                """);

        final Quest quest = setupQuest();
        new RunActionMigrator().migrate(quest);
        quest.saveAll();

        checkAssertion(quest, "package.yml");
    }

    @Test
    void migrate_run_action_with_complex_instructions() throws IOException, InvalidConfigurationException {
        original.loadFromString("""
                actions:
                  complex: "run ^tag add test ^give STONE:10 marked:quest ^location 100;200;300;world"
                """);
        expected.loadFromString("""
                actions:
                  complex:
                    1: "tag add test"
                    2: "give STONE:10 marked:quest"
                    3: "location 100;200;300;world"
                """);

        final Quest quest = setupQuest();
        new RunActionMigrator().migrate(quest);
        quest.saveAll();

        checkAssertion(quest, "package.yml");
    }

    @Test
    void migrate_run_action_with_multiple_spaces() throws IOException, InvalidConfigurationException {
        original.loadFromString("""
                actions:
                  spaced: "run  ^tag add beton  ^give emerald:5"
                """);
        expected.loadFromString("""
                actions:
                  spaced:
                    1: "tag add beton"
                    2: "give emerald:5"
                """);

        final Quest quest = setupQuest();
        new RunActionMigrator().migrate(quest);
        quest.saveAll();

        checkAssertion(quest, "package.yml");
    }

    @Test
    void migrate_run_action_preserves_order() throws IOException, InvalidConfigurationException {
        original.loadFromString("""
                actions:
                  ordered: "run ^first ^second ^third ^fourth ^fifth"
                """);
        expected.loadFromString("""
                actions:
                  ordered:
                    1: "first"
                    2: "second"
                    3: "third"
                    4: "fourth"
                    5: "fifth"
                """);

        final Quest quest = setupQuest();
        new RunActionMigrator().migrate(quest);
        quest.saveAll();

        checkAssertion(quest, "package.yml");
    }
}
