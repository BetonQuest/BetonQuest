package org.betonquest.betonquest.schedule;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.ScheduleIdentifier;
import org.betonquest.betonquest.api.instruction.section.SectionInstruction;
import org.betonquest.betonquest.api.schedule.Schedule;

/**
 * Factory for {@link Schedule} instances.
 *
 * @param <S> the scheduler type
 */
@FunctionalInterface
public interface ScheduleFactory<S extends Schedule> {

    /**
     * Create the Schedule from a section.
     *
     * @param scheduleID  the id of the schedule
     * @param instruction the section instruction
     * @return the created schedule
     * @throws QuestException when the creation fails
     */
    S createNewInstance(ScheduleIdentifier scheduleID, SectionInstruction instruction) throws QuestException;
}
