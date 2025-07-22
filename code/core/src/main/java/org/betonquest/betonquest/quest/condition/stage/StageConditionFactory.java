package org.betonquest.betonquest.quest.condition.stage;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.QuestTypeAPI;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.id.ObjectiveID;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.betonquest.betonquest.quest.condition.number.Operation;

/**
 * The stage condition factory class to create stage conditions.
 */
public class StageConditionFactory implements PlayerConditionFactory {

    /**
     * Quest Type API.
     */
    private final QuestTypeAPI questTypeAPI;

    /**
     * Creates the stage condition factory.
     *
     * @param questTypeAPI the Quest Type API
     */
    public StageConditionFactory(final QuestTypeAPI questTypeAPI) {
        this.questTypeAPI = questTypeAPI;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Variable<ObjectiveID> objectiveID = instruction.get(ObjectiveID::new);
        final Variable<Operation> operation = instruction.get(Operation::fromSymbol);
        final Variable<String> targetStage = instruction.get(Argument.STRING);
        return new StageCondition(questTypeAPI, objectiveID, targetStage, operation);
    }
}
