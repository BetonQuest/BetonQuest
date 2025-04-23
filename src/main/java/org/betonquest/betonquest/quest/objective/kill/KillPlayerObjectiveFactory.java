package org.betonquest.betonquest.quest.objective.kill;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.IDArgument;
import org.betonquest.betonquest.instruction.argument.VariableArgument;
import org.betonquest.betonquest.instruction.variable.VariableList;
import org.betonquest.betonquest.instruction.variable.VariableNumber;

/**
 * Factory for creating {@link KillPlayerObjective} instances from {@link Instruction}s.
 */
public class KillPlayerObjectiveFactory implements ObjectiveFactory {
    /**
     * Logger factory to create a logger for the objectives.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Creates a new instance of the KillPlayerObjectiveFactory.
     *
     * @param loggerFactory the logger factory to create a logger for the objectives
     */
    public KillPlayerObjectiveFactory(final BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public Objective parseInstruction(final Instruction instruction) throws QuestException {
        final VariableNumber targetAmount = instruction.get(VariableArgument.NUMBER_NOT_LESS_THAN_ONE);
        final String name = instruction.getOptional("name");
        final VariableList<ConditionID> required = instruction.get(instruction.getOptional("required", ""), IDArgument.ofList(ConditionID::new));
        final BetonQuestLogger log = loggerFactory.create(KillPlayerObjective.class);
        return new KillPlayerObjective(instruction, log, targetAmount, name, required);
    }
}
