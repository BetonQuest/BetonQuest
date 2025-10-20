package org.betonquest.betonquest.config.patcher.migration.migrator.from2to3;

import org.betonquest.betonquest.config.quest.Quest;
import org.betonquest.betonquest.config.quest.QuestFixture;
import org.bukkit.configuration.InvalidConfigurationException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * Test for dynamize Hologram Top Line.
 */
class DynamicHologramTopLineTest extends QuestFixture {

    @Test
    void migrate() throws InvalidConfigurationException, IOException {
        original.loadFromString("""
                holograms:
                  beton:
                    lines:
                    - 'item:custom_item'
                    - '&2Top questers this month'
                    - 'top:completed_quests;desc;10;&a;§6;2;&6'
                    - '&2Your amount: &6%point.completed_quests.amount%'
                    - '&Total amount: &6%azerothquests>globalpoint.total_completed_quests.amount%'
                    conditions: has_some_quest,!finished_some_quest
                    location: 100;200;300;world
                    check_interval: 20
                    max_range: 40
                """);
        final Quest quest = setupQuest("hologram.yml");
        new DynamicHologramTopLine().migrate(quest);
        quest.saveAll();
        expected.loadFromString("""
                holograms:
                  beton:
                    lines:
                    - 'item:custom_item'
                    - '&2Top questers this month'
                    - 'top:completed_quests;desc;10;§a{place}. §6{name}§2 - §6{score}'
                    - '&2Your amount: &6%point.completed_quests.amount%'
                    - '&Total amount: &6%azerothquests>globalpoint.total_completed_quests.amount%'
                    conditions: has_some_quest,!finished_some_quest
                    location: 100;200;300;world
                    check_interval: 20
                    max_range: 40
                """);
        checkAssertion(quest, "hologram.yml");
    }
}
