package org.betonquest.betonquest.config.patcher.migration.migrator.from1to2;

import org.betonquest.betonquest.config.quest.Quest;
import org.betonquest.betonquest.config.quest.QuestFixture;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for 'npc_effects' section migration.
 */
class EffectLibTest extends QuestFixture {
    @Test
    void test() throws IOException, InvalidConfigurationException {
        final YamlConfiguration original = new YamlConfiguration();
        original.loadFromString("""
                npc_effects:
                   check_interval: 50
                   disabled: false
                   farmer:
                      class: VortexEffect
                      iterations: 20
                      particle: crit_magic
                      helixes: 3
                      circles: 1
                      grow: 0.1
                      radius: 0.5
                      yaw: 90
                      pitch: 30
                      interval: 30
                      npcs:
                         - 1
                      conditions:
                         - '!con_tag_started'
                         - '!con_tag_finished'
                """);
        final Quest quest = setupQuest("effects.yml", original);
        new EffectLib().migrate(quest);
        final YamlConfiguration expected = new YamlConfiguration();
        expected.loadFromString("""
                effectlib:
                   farmer:
                      class: VortexEffect
                      iterations: 20
                      particle: crit_magic
                      helixes: 3
                      circles: 1
                      grow: 0.1
                      radius: 0.5
                      yaw: 90
                      pitch: -60
                      interval: 30
                      npcs:
                         - 1
                      conditions:
                         - '!con_tag_started'
                         - '!con_tag_finished'
                      checkinterval: 50
                """);
        assertEquals(expected.saveToString(), loadFile("effects.yml").saveToString(),
                "Migration result is not correct");
    }
}
