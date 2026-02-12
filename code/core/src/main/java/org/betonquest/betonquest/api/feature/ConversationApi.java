package org.betonquest.betonquest.api.feature;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.ConversationIdentifier;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.conversation.Conversation;
import org.betonquest.betonquest.conversation.DefaultConversationData;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

/**
 * Handles interaction with Conversations.
 */
public interface ConversationApi {

    /**
     * Gets stored Conversation Data.
     *
     * @param conversationID the id of the conversation
     * @return the loaded ConversationData
     * @throws QuestException if no ConversationData is loaded for the ID
     */
    DefaultConversationData getData(ConversationIdentifier conversationID) throws QuestException;

    /**
     * Creates and starts a conversation.
     *
     * @param onlineProfile  the profile to start the conversation for
     * @param conversationID the id of the conversation to start
     * @param center         the location where the conversation should start
     * @param startingOption the name of the option where the conversation should forcibly start at
     */
    void start(OnlineProfile onlineProfile, ConversationIdentifier conversationID, Location center, @Nullable String startingOption);

    /**
     * Checks if the player is in a conversation.
     *
     * @param profile the {@link Profile} of the player
     * @return if the player is the list of active conversations
     */
    boolean hasActive(Profile profile);

    /**
     * Gets this player's active conversation.
     *
     * @param profile the {@link Profile} of the player
     * @return player's active conversation or null if there is no conversation
     */
    @Nullable
    Conversation getActive(Profile profile);
}
