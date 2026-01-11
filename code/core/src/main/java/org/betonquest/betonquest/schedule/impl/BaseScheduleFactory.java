package org.betonquest.betonquest.schedule.impl;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.ActionIdentifier;
import org.betonquest.betonquest.api.instruction.argument.parser.EnumParser;
import org.betonquest.betonquest.api.instruction.section.SectionInstruction;
import org.betonquest.betonquest.api.schedule.CatchupStrategy;
import org.betonquest.betonquest.api.schedule.Schedule;
import org.betonquest.betonquest.schedule.ScheduleFactory;

import java.util.List;
import java.util.Optional;

/**
 * Schedule Factory that parses common schedule data.
 *
 * @param <S> the schedule type to create
 */
public abstract class BaseScheduleFactory<S extends Schedule> implements ScheduleFactory<S> {

    /**
     * Create a new Base Schedule Factory to create parse common schedule data.
     */
    public BaseScheduleFactory() {
    }

    /**
     * Parses the common objects required to create a schedule.
     *
     * @param instruction the section to load
     * @return the parsed objects
     * @throws QuestException when parts are missing or cannot be resolved
     */
    protected ScheduleData parseScheduleData(final SectionInstruction instruction) throws QuestException {
        final String time = Optional.ofNullable(instruction.getSection().getString("time"))
                .orElseThrow(() -> new QuestException("Missing time instruction"));
        final List<ActionIdentifier> actions;
        try {
            actions = instruction.read().value("actions").identifier(ActionIdentifier.class).list().get().getValue(null);
        } catch (final QuestException e) {
            throw new QuestException("Error while loading actions: " + e.getMessage(), e);
        }

        final String catchupString = instruction.getSection().getString("catchup");
        final CatchupStrategy catchup;
        if (catchupString == null) {
            catchup = CatchupStrategy.NONE;
        } else {
            catchup = new EnumParser<>(CatchupStrategy.class).apply(catchupString);
        }
        return new ScheduleData(time, actions, catchup);
    }

    /**
     * parsed objects required for schedule creation.
     *
     * @param time    Instruction string defining at which time the actions should be scheduled to run.
     * @param actions A list of actions that will be run by this schedule.
     * @param catchup Behavior for missed executions.
     */
    protected record ScheduleData(String time, List<ActionIdentifier> actions, CatchupStrategy catchup) {

    }
}
