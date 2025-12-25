package org.betonquest.betonquest.config.patcher.migration.migrator.from2to3;

import org.betonquest.betonquest.config.quest.Quest;
import org.betonquest.betonquest.config.quest.QuestFixture;
import org.bukkit.configuration.InvalidConfigurationException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * Test for renaming npc events.
 */
class NpcEventsRenameTest extends QuestFixture {

    @Test
    void migrate() throws InvalidConfigurationException, IOException {
        original.loadFromString("""
                events:
                  foo1: teleportnpc Bob 0;0;0;world
                  foo2: movenpc Bob 0;0;0;world
                  foo3: stopnpc Bob
                """);
        expected.loadFromString("""
                events:
                  foo1: npcteleport Bob 0;0;0;world
                  foo2: npcmove Bob 0;0;0;world
                  foo3: npcstop Bob
                """);

        final Quest quest = setupQuest("conv.yml");
        new NpcEventsRename().migrate(quest);
        quest.saveAll();

        checkAssertion(quest, "conv.yml");
    }
}
