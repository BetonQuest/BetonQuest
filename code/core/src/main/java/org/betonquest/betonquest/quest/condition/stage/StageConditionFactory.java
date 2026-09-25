package org.betonquest.betonquest.quest.condition.stage;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.ObjectiveIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.service.objective.ObjectiveManager;
import org.betonquest.betonquest.quest.condition.number.Operation;

/**
 * The stage condition factory class to create stage conditions.
 */
public class StageConditionFactory implements PlayerConditionFactory {

    /**
     * The objective manager.
     */
    private final ObjectiveManager objectiveManager;

    /**
     * Creates the stage condition factory.
     *
     * @param objectiveManager the objective manager
     */
    public StageConditionFactory(final ObjectiveManager objectiveManager) {
        this.objectiveManager = objectiveManager;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<ObjectiveIdentifier> objectiveID = instruction.identifier(ObjectiveIdentifier.class).get();
        final Argument<Operation> operation = instruction.parse(Operation::fromSymbol).get();
        final Argument<String> targetStage = instruction.string().get();
        return new StageCondition(objectiveManager, objectiveID, targetStage, operation);
    }
}
