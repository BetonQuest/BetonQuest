package org.betonquest.betonquest.schedule.impl;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.instruction.argument.parser.EnumParser;
import org.betonquest.betonquest.api.quest.Placeholders;
import org.betonquest.betonquest.api.quest.action.ActionID;
import org.betonquest.betonquest.api.schedule.CatchupStrategy;
import org.betonquest.betonquest.api.schedule.Schedule;
import org.betonquest.betonquest.lib.instruction.argument.DefaultListArgument;
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
     * The {@link Placeholders} to create and resolve placeholders.
     */
    private final Placeholders placeholders;

    /**
     * Quest package manager to get quest packages from.
     */
    private final QuestPackageManager packManager;

    /**
     * Create a new Base Schedule Factory to create parse common schedule data.
     *
     * @param placeholders the {@link Placeholders} to create and resolve placeholders
     * @param packManager  the quest package manager to get quest packages from
     */
    public BaseScheduleFactory(final Placeholders placeholders, final QuestPackageManager packManager) {
        this.placeholders = placeholders;
        this.packManager = packManager;
    }

    /**
     * Parses the common objects required to create a schedule.
     *
     * @param pack        source pack for argument and id resolving
     * @param instruction the section to load
     * @return the parsed objects
     * @throws QuestException when parts are missing or cannot be resolved
     */
    protected ScheduleData parseScheduleData(final QuestPackage pack, final ConfigurationSection instruction) throws QuestException {
        final String time = Optional.ofNullable(instruction.getString("time"))
                .orElseThrow(() -> new QuestException("Missing time instruction"));

        final String actionsString = Optional.ofNullable(instruction.getString("actions"))
                .orElseThrow(() -> new QuestException("Missing actions"));
        final List<ActionID> actions;
        try {
            actions = new DefaultListArgument<>(placeholders, pack, actionsString,
                    value -> new ActionID(placeholders, packManager, pack, value)).getValue(null);
        } catch (final QuestException e) {
            throw new QuestException("Error while loading actions: " + e.getMessage(), e);
        }

        final String catchupString = instruction.getString("catchup");
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
    protected record ScheduleData(String time, List<ActionID> actions, CatchupStrategy catchup) {

    }
}
