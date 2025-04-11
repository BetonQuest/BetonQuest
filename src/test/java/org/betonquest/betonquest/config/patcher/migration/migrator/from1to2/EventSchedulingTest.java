package org.betonquest.betonquest.config.patcher.migration.migrator.from1to2;

import org.betonquest.betonquest.config.quest.Quest;
import org.betonquest.betonquest.config.quest.QuestFixture;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for 'static' section migration.
 */
class EventSchedulingTest extends QuestFixture {
    @Test
    void test() throws IOException, InvalidConfigurationException {
        final YamlConfiguration original = new YamlConfiguration();
        original.loadFromString("""
                static:
                  '09:00': beton
                  '11:23': some_command,command_announcement
                """);
        final Quest quest = setupQuest("schedules.yml", original);
        new EventScheduling().migrate(quest);
        final YamlConfiguration expected = new YamlConfiguration();
        expected.loadFromString("""
                schedules:
                  '09:00':
                    type: realtime-daily
                    time: '09:00'
                    events: beton
                  '11:23':
                    type: realtime-daily
                    time: '11:23'
                    events: some_command,command_announcement
                """);
        assertEquals(quest.getQuestConfig().getKeys(true), expected.getKeys(true),
                "Expected \n" + expected.getKeys(true) + " but found \n" + quest.getQuestConfig().getKeys(true));
        assertEquals(expected.saveToString(), loadFile("schedules.yml").saveToString(),
                "The migration did not work correctly.");
    }
}
