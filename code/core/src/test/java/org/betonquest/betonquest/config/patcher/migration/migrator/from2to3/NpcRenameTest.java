package org.betonquest.betonquest.config.patcher.migration.migrator.from2to3;

import org.betonquest.betonquest.config.quest.Quest;
import org.betonquest.betonquest.config.quest.QuestFixture;
import org.bukkit.configuration.InvalidConfigurationException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * Test for Npc Variable (now named Placeholders) renaming.
 */
class NpcRenameTest extends QuestFixture {

    @Test
    void list_rename() throws IOException, InvalidConfigurationException {
        original.loadFromString("""
                one:
                  two:
                    - Value 1
                    - The %npc% is here
                  three:
                    - One
                    - Two
                """);
        final Quest quest = setupQuest("conv.yml");
        new NpcRename().migrate(quest);
        quest.saveAll();
        expected.loadFromString("""
                one:
                  two:
                    - Value 1
                    - The %quester% is here
                  three:
                    - One
                    - Two
                """);
        checkAssertion(quest, "conv.yml");
    }
}
