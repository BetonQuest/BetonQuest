package org.betonquest.betonquest.quest.condition.stage;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.id.ObjectiveID;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.VariableString;
import org.betonquest.betonquest.quest.condition.number.Operation;

/**
 * The stage condition factory class to create stage conditions.
 */
public class StageConditionFactory implements PlayerConditionFactory {

    /**
     * The BetonQuest instance.
     */
    private final BetonQuest betonQuest;

    /**
     * Creates the stage condition factory.
     *
     * @param betonQuest the BetonQuest instance
     */
    public StageConditionFactory(final BetonQuest betonQuest) {
        this.betonQuest = betonQuest;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final ObjectiveID objectiveID = instruction.getID(ObjectiveID::new);
        final Operation operation = Operation.fromSymbol(instruction.next());
        final VariableString targetStage = instruction.get(VariableString::new);
        return new StageCondition(objectiveID, targetStage, operation, betonQuest);
    }
}
