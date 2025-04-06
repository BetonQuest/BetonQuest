package org.betonquest.betonquest.quest.objective.arrow;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;

/**
 * Factory for creating {@link ArrowShootObjective} instances from {@link Instruction}s.
 */
public class ArrowShootObjectiveFactory implements ObjectiveFactory {

    /**
     * Logger factory to create a logger for the objectives.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Custom {@link BetonQuestLoggerFactory} instance for this class.
     *
     * @param loggerFactory the logger factory to create a logger for the objectives
     */
    public ArrowShootObjectiveFactory(final BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public Objective parseInstruction(final Instruction instruction) throws QuestException {
        final VariableLocation location = instruction.get(VariableLocation::new);
        final VariableNumber range = instruction.get(VariableNumber::new);
        final BetonQuestLogger log = loggerFactory.create(ArrowShootObjective.class);
        return new ArrowShootObjective(instruction, log, location, range);
    }
}
