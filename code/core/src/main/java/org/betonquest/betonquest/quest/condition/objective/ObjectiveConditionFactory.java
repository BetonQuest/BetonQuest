package org.betonquest.betonquest.quest.condition.objective;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.ObjectiveIdentifier;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;

/**
 * A factory for creating ObjectiveConditions.
 */
public class ObjectiveConditionFactory implements PlayerConditionFactory {

    /**
     * Quest Type API.
     */
    private final QuestTypeApi questTypeApi;

    /**
     * Creates a new ObjectiveConditionFactory.
     *
     * @param questTypeApi the Quest Type API
     */
    public ObjectiveConditionFactory(final QuestTypeApi questTypeApi) {
        this.questTypeApi = questTypeApi;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        return new ObjectiveCondition(questTypeApi, instruction.identifier(ObjectiveIdentifier.class).get());
    }
}
