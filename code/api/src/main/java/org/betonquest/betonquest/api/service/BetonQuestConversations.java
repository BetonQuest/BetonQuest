package org.betonquest.betonquest.api.service;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.identifier.ConversationIdentifier;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

/**
 * The BetonQuest conversations service is responsible for managing conversations.
 * <br> <br>
 * Conversations are the fundamental concept underlying pretty much all interactions in BetonQuest.
 * Each conversation is uniquely identified by a {@link ConversationIdentifier} which consists of the user-defined name
 * in the configuration as well as the {@link QuestPackage} the conversation belongs to.
 */
public interface BetonQuestConversations {

    /**
     * Evaluates whether a conversation can be started by a given {@link Profile}.
     * <br> <br>
     * A conversation can only be started if any starting option defined for the conversation is valid to start
     * based on its conditions.
     *
     * @param profile                the profile that wants to start the conversation
     * @param conversationIdentifier the identifier of the conversation to start
     * @return whether the conversation can be started
     * @throws QuestException if the conversation is not available
     */
    boolean canStart(Profile profile, ConversationIdentifier conversationIdentifier) throws QuestException;

    /**
     * Forcefully starts a conversation for a given {@link OnlineProfile} at a given {@link Location} and
     * with an optionally given starting option.
     * <br> <br>
     * Starting a conversation with a non-null starting option will ignore starting conditions
     * to start the conversation immediately. If no starting option is given,
     * the conversation will be started based on its starting options and their conditions.
     * <br> <br>
     * The location is required to force the player to stay at that specified location.
     * The precise behavior may differ based on the configured conversationIO.
     * <br> <br>
     * This method will trigger a PlayerConversationStartEvent that can be canceled.
     *
     * @param profile                the profile to start the conversation for
     * @param conversationIdentifier the identifier of the conversation to start
     * @param location               the location to force the player to stay at
     * @param startingOption         the optional starting option to start the conversation with
     */
    void start(OnlineProfile profile, ConversationIdentifier conversationIdentifier, Location location, @Nullable String startingOption);

    /**
     * Determines whether a {@link Profile} is currently in a conversation.
     *
     * @param profile the profile that might be in a conversation
     * @return true if the profile is currently in a conversation, false otherwise
     */
    boolean hasActive(Profile profile);
}
