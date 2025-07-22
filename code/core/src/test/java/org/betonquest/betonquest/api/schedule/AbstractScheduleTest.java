package org.betonquest.betonquest.api.schedule;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.schedule.ScheduleID;
import org.bukkit.configuration.ConfigurationSection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

/**
 * Abstract class that provides a basic test setup for testing schedules.
 * It mocks the {@link ScheduleID} of the schedule, it's {@link QuestPackage}
 * and the {@link ConfigurationSection} used for parsing the schedules' config.
 */
@ExtendWith(MockitoExtension.class)
public abstract class AbstractScheduleTest {

    /**
     * ID of the schedule to test.
     */
    @Mock
    protected ScheduleID scheduleID;

    /**
     * Quest package of the schedule to test.
     */
    @Mock
    protected QuestPackage questPackage;

    /**
     * Configuration section of the schedule to test.
     */
    @Mock
    protected ConfigurationSection section;

    /**
     * Before each test run prepare config and all mocks.
     */
    @BeforeEach
    protected void setupConfigs() {
        lenient().when(scheduleID.getPackage()).thenReturn(questPackage);
        prepareConfig();
    }

    /**
     * Method that creates a schedule instance with the provided ScheduleID and QuestPackage
     *
     * @return the new created schedule instance
     * @throws QuestException if parsing the schedule from the config failed
     */
    protected abstract Schedule createSchedule() throws QuestException;

    /**
     * Prepare the configuration section by configuring the mocks.
     */
    protected abstract void prepareConfig();
}
