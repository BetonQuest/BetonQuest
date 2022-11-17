package org.betonquest.betonquest.modules.schedule;

import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiConfiguration;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.modules.logger.util.BetonQuestLoggerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * This test should ensure that new ScheduleIDs can be constructed and throw an
 * ObjectNotFoundException if no schedule with that ID exists.
 */
@ExtendWith(BetonQuestLoggerService.class)
class ScheduleIDTest {

    @Test
    void testSectionExists() {
        final QuestPackage pack = mock(QuestPackage.class);
        final MultiConfiguration config = mock(MultiConfiguration.class);
        when(pack.getConfig()).thenReturn(config);
        when(config.isConfigurationSection("schedules.testSchedule")).thenReturn(true);

        assertDoesNotThrow(() -> new ScheduleID(pack, "testSchedule"),
                "Should not throw any exception on constructing a valid schedule id");
    }

    @Test
    void testNotaSection() {
        final QuestPackage pack = mock(QuestPackage.class);
        final MultiConfiguration config = mock(MultiConfiguration.class);
        when(pack.getConfig()).thenReturn(config);
        when(config.isConfigurationSection("schedules.testSchedule")).thenReturn(false);

        assertThrows(ObjectNotFoundException.class, () -> new ScheduleID(pack, "testSchedule"),
                "Should throw an exception if no schedule with this id exists");
    }
}
