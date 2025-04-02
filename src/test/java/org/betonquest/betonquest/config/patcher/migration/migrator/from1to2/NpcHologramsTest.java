package org.betonquest.betonquest.config.patcher.migration.migrator.from1to2;

import org.betonquest.betonquest.config.quest.Quest;
import org.betonquest.betonquest.config.quest.QuestFixture;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for Npc Holograms migration.
 */
class NpcHologramsTest extends QuestFixture {

    @Test
    void test() throws IOException, InvalidConfigurationException {
        final YamlConfiguration original = new YamlConfiguration();
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
        final Quest quest = setupQuest("holograms.yml", original);
        new NpcHolograms().migrate(quest);
        final YamlConfiguration expected = new YamlConfiguration();
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
        assertEquals(expected.saveToString(), loadFile("holograms.yml").saveToString(),
                "The hologram does not match the expected format");
    }
}
