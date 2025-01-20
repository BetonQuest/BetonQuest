package org.betonquest.betonquest.api.schedule;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.exception.ObjectNotFoundException;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.schedule.ScheduleID;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * <p>
 * A Schedule may be defined in any package.
 * It allows scheduling events to run for all online players at specific times.
 * </p>
 *
 * <p>
 * All types of Schedules must extend this superclass.
 * It should only be responsible for holding the data &amp; options of a single schedule.
 * The actual scheduling logic should be defined by extending {@link Scheduler}
 * </p>
 */
@SuppressWarnings({"PMD.AbstractClassWithoutAbstractMethod", "PMD.DataClass"})
public abstract class Schedule {

    /**
     * Identifier of this schedule.
     */
    protected final ScheduleID scheduleID;

    /**
     * Instruction string defining at which time the events should be scheduled to run.
     */
    protected final String time;

    /**
     * A list of events that will be run by this schedule.
     */
    protected final List<EventID> events;

    /**
     * Defines how the scheduler should behave if an execution of the schedule was missed
     * (e.g., due to a shutdown of the server).
     * Should be None by default.
     */
    protected final CatchupStrategy catchup;

    /**
     * Creates new instance of the schedule.
     * It should parse all options from the configuration section.
     * If anything goes wrong, throw {@link QuestException} with an error message describing the problem.
     *
     * @param scheduleID  id of the new schedule
     * @param instruction config defining the schedule
     * @throws QuestException if parsing the config failed
     */
    public Schedule(final ScheduleID scheduleID, final ConfigurationSection instruction) throws QuestException {
        this.scheduleID = scheduleID;

        this.time = Optional.ofNullable(instruction.getString("time"))
                .orElseThrow(() -> new QuestException("Missing time instruction"));

        final String eventsString = Optional.ofNullable(instruction.getString("events"))
                .orElseThrow(() -> new QuestException("Missing events"));
        final List<EventID> events = new ArrayList<>();
        for (final String eventId : eventsString.split(",")) {
            try {
                events.add(new EventID(scheduleID.getPackage(), eventId));
            } catch (final ObjectNotFoundException e) {
                throw new QuestException("Error while loading events: " + e.getMessage(), e);
            }
        }
        this.events = Collections.unmodifiableList(events);

        final String catchupString = instruction.getString("catchup");
        try {
            this.catchup = Optional.ofNullable(catchupString).map(String::toUpperCase).map(CatchupStrategy::valueOf).orElse(CatchupStrategy.NONE);
        } catch (final IllegalArgumentException e) {
            throw new QuestException("There is no such catchup strategy: " + catchupString, e);
        }
    }

    /**
     * Get the Identifier of this schedule.
     *
     * @return the id
     */
    public ScheduleID getId() {
        return scheduleID;
    }

    /**
     * Get the Instruction string defining at which time the events should be scheduled to run.
     *
     * @return string defined with key {@code time } in the config section of the schedule
     */
    public String getTime() {
        return time;
    }

    /**
     * Get a list of events that will be run by this schedule.
     *
     * @return unmodifiable list of events
     */
    public List<EventID> getEvents() {
        return events;
    }

    /**
     * Get how the scheduler should behave if an execution of the schedule was missed
     * (e.g., due to a shutdown of the server).
     *
     * @return the catchup strategy, {@link CatchupStrategy#NONE} by default
     */
    public CatchupStrategy getCatchup() {
        return catchup;
    }
}
