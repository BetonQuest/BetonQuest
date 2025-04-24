package org.betonquest.betonquest.quest.objective.delay;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.variable.Variable;

/**
 * Factory for creating {@link DelayObjective} instances from {@link Instruction}s.
 */
public class DelayObjectiveFactory implements ObjectiveFactory {

    /**
     * Logger factory to create a logger for the objectives.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Creates a new instance of the DelayObjectiveFactory.
     *
     * @param loggerFactory the logger factory to create a logger for the objectives
     */
    public DelayObjectiveFactory(final BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public Objective parseInstruction(final Instruction instruction) throws QuestException {
        final Variable<Number> delay = instruction.getVariable(Argument.NUMBER_NOT_LESS_THAN_ZERO);
        final int interval = instruction.getInt(instruction.getOptional("interval"), 20 * 10);
        if (interval <= 0) {
            throw new QuestException("Interval cannot be less than 1 tick");
        }
        final BetonQuestLogger log = loggerFactory.create(DelayObjective.class);
        return new DelayObjective(instruction, log, interval, delay);
    }
}
