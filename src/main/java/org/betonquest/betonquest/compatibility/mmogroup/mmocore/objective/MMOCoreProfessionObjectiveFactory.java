package org.betonquest.betonquest.compatibility.mmogroup.mmocore.objective;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.variable.Variable;

/**
 * Factory for creating {@link MMOCoreProfessionObjective} instances from {@link Instruction}s.
 */
public class MMOCoreProfessionObjectiveFactory implements ObjectiveFactory {
    /**
     * Logger factory to create a logger for the objectives.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Creates a new instance of the MMOCoreProfessionObjectiveFactory.
     *
     * @param loggerFactory the logger factory to create a logger for the objectives
     */
    public MMOCoreProfessionObjectiveFactory(final BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public Objective parseInstruction(final Instruction instruction) throws QuestException {
        final String profession = instruction.next();
        final String professionName = "MAIN".equalsIgnoreCase(profession) ? null : profession;
        final Variable<Number> targetLevel = instruction.getVariable(Argument.NUMBER);
        final BetonQuestLogger log = loggerFactory.create(MMOCoreProfessionObjective.class);
        return new MMOCoreProfessionObjective(instruction, log, professionName, targetLevel);
    }
}
