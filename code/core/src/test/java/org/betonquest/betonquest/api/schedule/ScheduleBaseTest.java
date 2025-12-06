package org.betonquest.betonquest.api.schedule;

import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiConfiguration;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.logger.util.BetonQuestLoggerService;
import org.betonquest.betonquest.schedule.impl.BaseScheduleFactory;
import org.bukkit.configuration.ConfigurationOptions;
import org.bukkit.configuration.ConfigurationSection;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * These tests should ensure that the basic parsing of schedules works properly.
 */
@SuppressWarnings("PMD.JUnit5TestShouldBePackagePrivate")
@ExtendWith(BetonQuestLoggerService.class)
public class ScheduleBaseTest extends AbstractScheduleTest {

    @Override
    protected Schedule createSchedule() throws QuestException {
        return new BaseScheduleFactory<>(variables, packManager) {
            @Override
            public Schedule createNewInstance(final ScheduleID scheduleID, final ConfigurationSection config)
                    throws QuestException {
                final ScheduleData scheduleData = parseScheduleData(scheduleID.getPackage(), config);
                return new Schedule(scheduleID, scheduleData.events(), scheduleData.catchup()) {
                };
            }
        }.createNewInstance(scheduleID, section);
    }

    @Override
    protected void prepareConfig() {
        final MultiConfiguration mockConfig = mock(MultiConfiguration.class);
        lenient().when(questPackage.getConfig()).thenReturn(mockConfig);
        lenient().when(mockConfig.getString("events.bell_ring")).thenReturn("folder bell_lever_toggle,bell_lever_toggle period:0.5");
        lenient().when(mockConfig.getString("events.notify_goodNight")).thenReturn("notify &6Good night, sleep well!");
        final ConfigurationOptions configurationOptions = mock(ConfigurationOptions.class);
        lenient().when(configurationOptions.pathSeparator()).thenReturn('.');
        lenient().when(mockConfig.options()).thenReturn(configurationOptions);

        lenient().when(section.getString("time")).thenReturn("22:00");
        lenient().when(section.getString("events")).thenReturn("bell_ring,notify_goodNight");
        lenient().when(section.getString("catchup")).thenReturn("NONE");
    }

    /**
     * Test once if a schedule with the provided sample configuration loads all values successfully.
     *
     * @throws QuestException if parsing the schedule failed, test should fail
     */
    @Test
    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    public void testScheduleValidLoad() throws QuestException {
        final Schedule schedule = createSchedule();
        assertEquals(scheduleID, schedule.getId(), "Schedule should return the id it was constructed with");
        assertEquals(CatchupStrategy.NONE, schedule.getCatchup(), "Returned catchup strategy should be correct");
        assertEquals("bell_ring", schedule.getEvents().get(0).get(), "Returned events should contain 1st event");
        assertEquals("notify_goodNight", schedule.getEvents().get(1).get(), "Returned events should contain 2nd event");
    }

    @Test
    void testTimeNotSet() {
        when(section.getString("time")).thenReturn(null);
        final QuestException exception = assertThrows(QuestException.class, this::createSchedule, "Schedule should throw instruction parse exception for invalid time");
        assertEquals("Missing time instruction", exception.getMessage(), "QuestException should have correct reason message");
    }

    @Test
    void testEventsNotSet() {
        when(section.getString("events")).thenReturn(null);
        final QuestException exception = assertThrows(QuestException.class, this::createSchedule, "Schedule should throw instruction parse exception for missing events");
        assertEquals("Missing events", exception.getMessage(), "QuestException should have correct reason message");
    }

    @Test
    void testEventsNotFound() {
        when(questPackage.getConfig().getString("events.bell_ring")).thenReturn(null);
        final QuestException exception = assertThrows(QuestException.class, this::createSchedule, "Schedule should throw instruction parse exception for invalid event names");
        assertInstanceOf(QuestException.class, exception.getCause(), "Cause should be QuestException");
    }

    @Test
    void testInvalidCatchup() {
        when(section.getString("catchup")).thenReturn("NotExistingCatchupStrategy");
        final QuestException exception = assertThrows(QuestException.class, this::createSchedule, "Schedule should throw instruction parse exception for invalid catchup");
        assertEquals("Invalid enum value 'NOTEXISTINGCATCHUPSTRATEGY' for type 'CatchupStrategy'", exception.getMessage(), "QuestException should have correct reason message");
    }

    @Test
    void testNoCatchup() throws QuestException {
        when(section.getString("catchup")).thenReturn(null);
        final Schedule schedule = createSchedule();
        assertEquals(CatchupStrategy.NONE, schedule.getCatchup(), "Returned catchup strategy should be correct");
    }

    @Test
    void testLowerCaseCatchup() throws QuestException {
        when(section.getString("catchup")).thenReturn("one");
        final Schedule schedule = createSchedule();
        assertEquals(CatchupStrategy.ONE, schedule.getCatchup(), "Returned catchup strategy should be correct");
    }
}
