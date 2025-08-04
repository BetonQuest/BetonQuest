package org.betonquest.betonquest.quest.condition.objective;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.id.ObjectiveID;
import org.betonquest.betonquest.instruction.Instruction;

/**
 * A factory for creating ObjectiveConditions.
 */
public class ObjectiveConditionFactory implements PlayerConditionFactory {

    /**
     * Quest Type API.
     */
    private final QuestTypeApi questTypeAPI;

    /**
     * Creates a new ObjectiveConditionFactory.
     *
     * @param questTypeAPI the Quest Type API
     */
    public ObjectiveConditionFactory(final QuestTypeApi questTypeAPI) {
        this.questTypeAPI = questTypeAPI;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        return new ObjectiveCondition(questTypeAPI, instruction.get(ObjectiveID::new));
    }
}
