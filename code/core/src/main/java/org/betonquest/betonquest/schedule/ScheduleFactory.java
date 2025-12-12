package org.betonquest.betonquest.schedule;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.schedule.Schedule;
import org.betonquest.betonquest.api.schedule.ScheduleID;
import org.bukkit.configuration.ConfigurationSection;

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
     * @param scheduleID the id of the schedule
     * @param config     the section to load the schedule from
     * @return the created schedule
     * @throws QuestException when the creation fails
     */
    S createNewInstance(ScheduleID scheduleID, ConfigurationSection config) throws QuestException;
}
