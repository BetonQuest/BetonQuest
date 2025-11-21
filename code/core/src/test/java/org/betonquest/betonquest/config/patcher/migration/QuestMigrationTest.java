package org.betonquest.betonquest.config.patcher.migration;

import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiConfiguration;
import org.betonquest.betonquest.config.quest.Quest;
import org.betonquest.betonquest.config.quest.QuestFixture;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

/**
 * Generic tests for Migration types.
 */
class QuestMigrationTest extends QuestFixture {

    @Test
    void test_flat() throws IOException, InvalidConfigurationException {
        original.loadFromString("""
                old:
                    type: beton
                """);
        final Quest quest = setupQuest("other.yml");
        new RenameSection("old", "new").migrate(quest);
        quest.saveAll();
        expected.loadFromString("""
                new:
                    type: beton
                """);
        checkAssertion(quest, "other.yml");
    }

    @Test
    void test_deep() throws IOException, InvalidConfigurationException {
        original.loadFromString("""
                old:
                  avc:
                    type: beton
                """);
        final Quest quest = setupQuest("other.yml");
        new RenameSection("old", "new").migrate(quest);
        quest.saveAll();
        expected.loadFromString("""
                new:
                  avc:
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

    /**
     * Renames a section.
     *
     * @param oldPath the old path to remove
     * @param newPath the new path to set old values
     */
    private record RenameSection(String oldPath, String newPath) implements QuestMigration {

        @Override
        public void migrate(final Quest quest) throws InvalidConfigurationException {
            final MultiConfiguration config = quest.getQuestConfig();
            final ConfigurationSection staticSection = config.getConfigurationSection(oldPath);
            final ConfigurationSection source = config.getSourceConfigurationSection(oldPath);
            if (staticSection == null || source == null) {
                return;
            }
            for (final Map.Entry<String, Object> entry : staticSection.getValues(false).entrySet()) {
                final String key = entry.getKey();
                final Object value = entry.getValue();
                config.set(newPath + "." + key, value);
            }
            config.set(oldPath, null);
            config.associateWith(source);
        }
    }
}
