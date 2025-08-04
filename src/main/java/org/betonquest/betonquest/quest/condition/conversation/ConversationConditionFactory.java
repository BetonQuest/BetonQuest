package org.betonquest.betonquest.quest.condition.conversation;

import org.betonquest.betonquest.api.feature.FeatureApi;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.id.ConversationID;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.Variable;

/**
 * A factory for creating ConversationCondition objects.
 */
public class ConversationConditionFactory implements PlayerConditionFactory {

    /**
     * Feature API.
     */
    private final FeatureApi featureAPI;

    /**
     * Creates a new ConversationConditionFactory.
     *
     * @param featureAPI the feature API
     */
    public ConversationConditionFactory(final FeatureApi featureAPI) {
        this.featureAPI = featureAPI;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Variable<ConversationID> conversationID = instruction.get(ConversationID::new);
        return new ConversationCondition(featureAPI, conversationID);
    }
}
