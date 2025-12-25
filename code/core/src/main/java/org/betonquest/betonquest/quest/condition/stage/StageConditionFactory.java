package org.betonquest.betonquest.quest.condition.stage;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.objective.ObjectiveID;
import org.betonquest.betonquest.quest.condition.number.Operation;

/**
 * The stage condition factory class to create stage conditions.
 */
public class StageConditionFactory implements PlayerConditionFactory {

    /**
     * Quest Type API.
     */
    private final QuestTypeApi questTypeApi;

    /**
     * Creates the stage condition factory.
     *
     * @param questTypeApi the Quest Type API
     */
    public StageConditionFactory(final QuestTypeApi questTypeApi) {
        this.questTypeApi = questTypeApi;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<ObjectiveID> objectiveID = instruction.parse(ObjectiveID::new).get();
        final Argument<Operation> operation = instruction.parse(Operation::fromSymbol).get();
        final Argument<String> targetStage = instruction.string().get();
        return new StageCondition(questTypeApi, objectiveID, targetStage, operation);
    }
}
