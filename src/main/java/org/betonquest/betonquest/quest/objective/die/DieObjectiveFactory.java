package org.betonquest.betonquest.quest.objective.die;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.bukkit.Location;

/**
 * Factory for creating {@link DieObjective} instances from {@link Instruction}s.
 */
public class DieObjectiveFactory implements ObjectiveFactory {

    /**
     * Logger factory to create a logger for the objectives.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Creates a new instance of the DieObjectiveFactory.
     *
     * @param loggerFactory the logger factory to create a logger for the objectives
     */
    public DieObjectiveFactory(final BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public Objective parseInstruction(final Instruction instruction) throws QuestException {
        final boolean cancel = instruction.hasArgument("cancel");
        final Variable<Location> location = instruction.getVariable(instruction.getOptional("respawn"), Argument.LOCATION);
        final BetonQuestLogger log = loggerFactory.create(DieObjective.class);
        return new DieObjective(instruction, log, cancel, location);
    }
}
