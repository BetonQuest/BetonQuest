package org.betonquest.betonquest.schedule.impl;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.instruction.variable.VariableList;
import org.betonquest.betonquest.api.quest.Variables;
import org.betonquest.betonquest.api.quest.event.EventID;
import org.betonquest.betonquest.api.schedule.CatchupStrategy;
import org.betonquest.betonquest.api.schedule.Schedule;
import org.betonquest.betonquest.schedule.ScheduleFactory;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.Optional;

/**
 * Schedule Factory that parses common schedule data.
 *
 * @param <S> the schedule type to create
 */
public abstract class BaseScheduleFactory<S extends Schedule> implements ScheduleFactory<S> {

    /**
     * Variable processor to create and resolve variables.
     */
    private final Variables variables;

    /**
     * Quest package manager to get quest packages from.
     */
    private final QuestPackageManager packManager;

    /**
     * Create a new Base Schedule Factory to create parse common schedule data.
     *
     * @param variables   the variable processor to create and resolve variables
     * @param packManager the quest package manager to get quest packages from
     */
    public BaseScheduleFactory(final Variables variables, final QuestPackageManager packManager) {
        this.variables = variables;
        this.packManager = packManager;
    }

    /**
     * Parses the common objects required to create a schedule.
     *
     * @param pack        source pack for variable and id resolving
     * @param instruction the section to load
     * @return the parsed objects
     * @throws QuestException when parts are missing or cannot be resolved
     */
    protected ScheduleData parseScheduleData(final QuestPackage pack, final ConfigurationSection instruction) throws QuestException {
        final String time = Optional.ofNullable(instruction.getString("time"))
                .orElseThrow(() -> new QuestException("Missing time instruction"));

        final String eventsString = Optional.ofNullable(instruction.getString("events"))
                .orElseThrow(() -> new QuestException("Missing events"));
        final List<EventID> events;
        try {
            events = new VariableList<>(variables, pack, eventsString,
                    value -> new EventID(variables, packManager, pack, value)).getValue(null);
        } catch (final QuestException e) {
            throw new QuestException("Error while loading events: " + e.getMessage(), e);
        }

        final String catchupString = instruction.getString("catchup");
        final CatchupStrategy catchup;
        if (catchupString == null) {
            catchup = CatchupStrategy.NONE;
        } else {
            catchup = Argument.ENUM(CatchupStrategy.class).apply(catchupString);
        }
        return new ScheduleData(time, events, catchup);
    }

    /**
     * parsed objects required for schedule creation.
     *
     * @param time    Instruction string defining at which time the events should be scheduled to run.
     * @param events  A list of events that will be run by this schedule.
     * @param catchup Behavior for missed executions.
     */
    protected record ScheduleData(String time, List<EventID> events, CatchupStrategy catchup) {

    }
}
