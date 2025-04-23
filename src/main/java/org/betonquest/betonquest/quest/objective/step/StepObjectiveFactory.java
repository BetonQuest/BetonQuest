package org.betonquest.betonquest.quest.objective.step;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.betonquest.betonquest.util.BlockSelector;
import org.bukkit.Location;

/**
 * Factory for creating {@link StepObjective} instances from {@link Instruction}s.
 */
public class StepObjectiveFactory implements ObjectiveFactory {
    /**
     * Logger factory to create a logger for the objectives.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Creates a new StepObjectiveFactory instance.
     *
     * @param loggerFactory the logger factory to create a logger for the objectives
     */
    public StepObjectiveFactory(final BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public Objective parseInstruction(final Instruction instruction) throws QuestException {
        final Variable<Location> loc = instruction.getVariable(Argument.LOCATION);
        final BlockSelector selector = new BlockSelector(".*_PRESSURE_PLATE");
        final BetonQuestLogger log = loggerFactory.create(StepObjective.class);
        return new StepObjective(instruction, log, loc, selector);
    }
}
