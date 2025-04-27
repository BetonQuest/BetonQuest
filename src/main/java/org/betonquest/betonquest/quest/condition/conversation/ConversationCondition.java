package org.betonquest.betonquest.quest.condition.conversation;

import org.betonquest.betonquest.api.feature.FeatureAPI;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.id.ConversationID;
import org.betonquest.betonquest.instruction.variable.Variable;

/**
 * Checks if the conversation with player has at least one possible option.
 */
public class ConversationCondition implements PlayerCondition {

    /**
     * Feature API.
     */
    private final FeatureAPI featureAPI;

    /**
     * The conversation to check.
     */
    private final Variable<ConversationID> conversationID;

    /**
     * Creates a new ConversationCondition.
     *
     * @param featureAPI     the feature API
     * @param conversationID the conversation to check
     */
    public ConversationCondition(final FeatureAPI featureAPI, final Variable<ConversationID> conversationID) {
        this.featureAPI = featureAPI;
        this.conversationID = conversationID;
    }

    @Override
    public boolean check(final Profile profile) throws QuestException {
        return featureAPI.getConversation(conversationID.getValue(profile)).isReady(profile);
    }
}
