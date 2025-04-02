package org.betonquest.betonquest.config.patcher.migration.migrator.from1to2;

import org.betonquest.betonquest.config.quest.Quest;
import org.betonquest.betonquest.config.quest.QuestFixture;
import org.bukkit.configuration.InvalidConfigurationException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * Test for Npc Holograms migration.
 */
class NpcHologramsTest extends QuestFixture {

    @Test
    void migrate() throws IOException, InvalidConfigurationException {
        original.loadFromString("""
                npc_holograms:
                  check_interval: 200
                  follow: true
                  default:
                    lines:
                      - "Some text!"
                    conditions: "has_some_quest"
                    vector: 0;3;0
                    npcs:
                      - 0
                      - 22
                """);
        final Quest quest = setupQuest("holograms.yml");
        new NpcHolograms().migrate(quest);
        quest.saveAll();
        expected.loadFromString("""
                npc_holograms:
                  default:
                    lines:
                      - "Some text!"
                    conditions: "has_some_quest"
                    npcs:
                      - 0
                      - 22
                    follow: true
                """);
        checkAssertion(quest, "holograms.yml");
    }
}
