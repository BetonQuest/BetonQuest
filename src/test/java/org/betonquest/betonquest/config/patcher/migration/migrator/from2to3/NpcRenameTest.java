package org.betonquest.betonquest.config.patcher.migration.migrator.from2to3;

import org.betonquest.betonquest.config.quest.Quest;
import org.betonquest.betonquest.config.quest.QuestFixture;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for Npc Variable renaming.
 */
class NpcRenameTest extends QuestFixture {

    @Test
    void test_list_rename() throws IOException, InvalidConfigurationException {
        final YamlConfiguration original = new YamlConfiguration();
        original.loadFromString("""
                one:
                  two:
                    - Value 1
                    - The %npc% is here
                  three:
                    - One
                    - Two
                """);
        final Quest quest = setupQuest("conv.yml", original);
        new NpcRename().migrate(quest);
        final YamlConfiguration expected = new YamlConfiguration();
        expected.loadFromString("""
                one:
                  two:
                    - Value 1
                    - The %quester% is here
                  three:
                    - One
                    - Two
                """);
        assertEquals(quest.getQuestConfig().getKeys(true), expected.getKeys(true),
                "Expected \n" + expected.getKeys(true) + " but found \n" + quest.getQuestConfig().getKeys(true));
        assertEquals(expected.saveToString(), loadFile("conv.yml").saveToString(),
                "The change should be saved");
    }
}
