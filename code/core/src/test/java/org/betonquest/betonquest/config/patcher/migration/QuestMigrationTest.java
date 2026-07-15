package org.betonquest.betonquest.config.patcher.migration;

import org.betonquest.betonquest.api.config.section.multi.MultiConfiguration;
import org.betonquest.betonquest.config.quest.QuestFixture;
import org.betonquest.betonquest.lib.config.patcher.migration.QuestMigration;
import org.betonquest.betonquest.lib.config.quest.Quest;
import org.bukkit.configuration.InvalidConfigurationException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * Generic tests for Migration types.
 */
class QuestMigrationTest extends QuestFixture {

    @Test
    void test_flat() throws IOException, InvalidConfigurationException {
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
    void test_deep() throws IOException, InvalidConfigurationException {
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
    void test_nested() throws IOException, InvalidConfigurationException {
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
    void migrateSubSectionsForSingeLineInstructions() throws InvalidConfigurationException, IOException {
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
}
