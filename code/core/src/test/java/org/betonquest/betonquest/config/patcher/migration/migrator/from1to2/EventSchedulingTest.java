package org.betonquest.betonquest.config.patcher.migration.migrator.from1to2;

import org.betonquest.betonquest.config.quest.Quest;
import org.betonquest.betonquest.config.quest.QuestFixture;
import org.bukkit.configuration.InvalidConfigurationException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * Test for 'static' section migration.
 */
class EventSchedulingTest extends QuestFixture {

    @Test
    void migrate() throws IOException, InvalidConfigurationException {
        original.loadFromString("""
                static:
                  '09:00': beton
                  '11:23': some_command,command_announcement
                """);
        final Quest quest = setupQuest("schedules.yml");
        new EventScheduling().migrate(quest);
        quest.saveAll();
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
        checkAssertion(quest, "schedules.yml");
    }
}
