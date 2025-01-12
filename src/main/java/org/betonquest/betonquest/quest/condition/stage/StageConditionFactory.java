package org.betonquest.betonquest.quest.condition.stage;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.id.ObjectiveID;
import org.betonquest.betonquest.instruction.variable.VariableString;
import org.betonquest.betonquest.quest.condition.number.Operation;
import org.betonquest.betonquest.quest.registry.processor.VariableProcessor;

/**
 * The stage condition factory class to create stage conditions.
 */
public class StageConditionFactory implements PlayerConditionFactory {

    /**
     * The variable processor.
     */
    private final VariableProcessor variableProcessor;

    /**
     * The BetonQuest instance.
     */
    private final BetonQuest betonQuest;

    /**
     * Creates the stage condition factory.
     *
     * @param variableProcessor the variable processor
     * @param betonQuest        the BetonQuest instance
     */
    public StageConditionFactory(final VariableProcessor variableProcessor, final BetonQuest betonQuest) {
        this.variableProcessor = variableProcessor;
        this.betonQuest = betonQuest;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final ObjectiveID objectiveID = instruction.getObjective();
        final Operation operation = Operation.fromSymbol(instruction.next());
        final VariableString targetStage = new VariableString(variableProcessor, instruction.getPackage(), instruction.next());
        return new StageCondition(objectiveID, targetStage, operation, betonQuest);
    }
}
