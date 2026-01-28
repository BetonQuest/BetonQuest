package org.betonquest.betonquest.api.schedule;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiConfiguration;
import org.betonquest.betonquest.api.identifier.ScheduleIdentifier;
import org.betonquest.betonquest.api.instruction.section.SectionInstruction;
import org.betonquest.betonquest.logger.util.BetonQuestLoggerService;
import org.betonquest.betonquest.schedule.impl.BaseScheduleFactory;
import org.bukkit.configuration.ConfigurationOptions;
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
        return new BaseScheduleFactory<>() {
            @Override
            public Schedule createNewInstance(final ScheduleIdentifier scheduleID, final SectionInstruction instruction)
                    throws QuestException {
                final ScheduleData scheduleData = parseScheduleData(instruction);
                return new Schedule(scheduleID, scheduleData.actions(), scheduleData.catchup()) {
                };
            }
        }.createNewInstance(scheduleID, getMockedInstruction());
    }

    @Override
    protected void prepareConfig() {
        final MultiConfiguration mockConfig = mock(MultiConfiguration.class);
        lenient().when(questPackage.getConfig()).thenReturn(mockConfig);
        lenient().when(mockConfig.getString("actions.bell_ring")).thenReturn("folder bell_lever_toggle,bell_lever_toggle period:0.5");
        lenient().when(mockConfig.isString("actions.bell_ring")).thenReturn(true);
        lenient().when(mockConfig.getString("actions.notify_goodNight")).thenReturn("notify &6Good night, sleep well!");
        lenient().when(mockConfig.isString("actions.notify_goodNight")).thenReturn(true);
        final ConfigurationOptions configurationOptions = mock(ConfigurationOptions.class);
        lenient().when(configurationOptions.pathSeparator()).thenReturn('.');
        lenient().when(mockConfig.options()).thenReturn(configurationOptions);

        lenient().when(section.getString("time")).thenReturn("22:00");
        lenient().when(section.contains("time")).thenReturn(true);
        lenient().when(section.getString("actions")).thenReturn("bell_ring,notify_goodNight");
        lenient().when(section.contains("actions")).thenReturn(true);
        lenient().when(section.getString("catchup")).thenReturn("NONE");
        lenient().when(section.contains("catchup")).thenReturn(true);
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
        assertEquals("bell_ring", schedule.getActions().get(0).get(), "Returned actions should contain 1st action");
        assertEquals("notify_goodNight", schedule.getActions().get(1).get(), "Returned actions should contain 2nd action");
        assertEquals(2, schedule.getActions().size(), "Returned actions should contain 2 actions");
    }

    @Test
    void testTimeNotSet() {
        when(section.getString("time")).thenReturn(null);
        lenient().when(section.contains("time")).thenReturn(false);
        assertThrows(QuestException.class, this::createSchedule, "Schedule should throw instruction parse exception for invalid time");
    }

    @Test
    void testActionsNotSet() {
        when(section.contains("actions")).thenReturn(false);
        assertThrows(QuestException.class, this::createSchedule, "Schedule should throw instruction parse exception for missing actions");
    }

    @Test
    void testActionsNotFound() {
        when(section.getString("actions")).thenReturn("bell_ring,notify_goodNight,action_does_not_exist");
        final QuestException exception = assertThrows(QuestException.class, this::createSchedule, "Schedule should throw instruction parse exception for invalid action names");
        assertInstanceOf(QuestException.class, exception.getCause(), "Cause should be QuestException");
    }

    @Test
    void testInvalidCatchup() {
        when(section.getString("catchup")).thenReturn("NotExistingCatchupStrategy");
        assertThrows(QuestException.class, this::createSchedule, "Schedule should throw instruction parse exception for invalid catchup");
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
