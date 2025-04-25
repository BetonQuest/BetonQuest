package org.betonquest.betonquest.quest.objective.resourcepack;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.variable.Variable;

/**
 * Factory for creating {@link ResourcepackObjective} instances from {@link Instruction}s.
 */
public class ResourcepackObjectiveFactory implements ObjectiveFactory {
    /**
     * Logger factory to create a logger for the objectives.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Creates a new instance of the ResourcepackObjectiveFactory.
     *
     * @param loggerFactory the logger factory to create a logger for the objectives
     */
    public ResourcepackObjectiveFactory(final BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public Objective parseInstruction(final Instruction instruction) throws QuestException {
        final Variable<String> targetStatus = instruction.getVariable(Argument.STRING);
        final BetonQuestLogger log = loggerFactory.create(ResourcepackObjective.class);
        return new ResourcepackObjective(instruction, log, targetStatus);
    }
}
