package org.betonquest.betonquest.api.service.conversation;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.identifier.ConversationIdentifier;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * The BetonQuest conversations service is responsible for managing conversations.
 * <br> <br>
 * Conversations are the fundamental concept underlying pretty much all interactions in BetonQuest.
 * Each conversation is uniquely identified by a {@link ConversationIdentifier} which consists of the user-defined name
 * in the configuration as well as the {@link QuestPackage} the conversation belongs to.
 *
 * @since 3.0.0
 */
public interface Conversations {

    /**
     * Evaluates whether a conversation can be started by a given {@link Profile}.
     * <br> <br>
     * A conversation can only be started if any starting option defined for the conversation
     * has its conditions fulfilled.
     *
     * @param profile                the profile that wants to start the conversation
     * @param conversationIdentifier the identifier of the conversation to start
     * @return whether the conversation can be started
     * @throws QuestException if the conversation is not available
     * @since 3.0.0
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
     * @since 3.0.0
     */
    void start(OnlineProfile profile, ConversationIdentifier conversationIdentifier, Location location, @Nullable String startingOption);

    /**
     * Determines whether a {@link Profile} is currently in a conversation.
     *
     * @param profile the profile that might be in a conversation
     * @return true if the profile is currently in a conversation, false otherwise
     * @since 3.0.0
     */
    boolean hasActive(Profile profile);

    /**
     * Cancels the active conversation for the given profile.
     * If the profile is not currently in a conversation, this method will do nothing.
     *
     * @param profile the profile to cancel the conversation for
     * @since 3.0.0
     */
    void cancel(OnlineProfile profile);

    /**
     * Gets the display name of the quester for the given profile in the active conversation.
     *
     * @param profile the profile to get the quester name for
     * @return the display name of the quester or an empty optional if no conversation is active or an error occurs
     * @since 3.0.0
     */
    Optional<Component> getActiveQuesterName(Profile profile);

    /**
     * Sends a bypass message to the given profile that gets displayed to the player of the online profile
     * even if the profile is currently engaged in a conversation.
     * <br>
     * If the profile is not engaged in a conversation, this method will simply send the message to the player.
     *
     * @param profile the profile to send the bypass message to
     * @param message the message to send
     * @since 3.0.0
     */
    void sendBypassMessage(OnlineProfile profile, Component message);

    /**
     * If the given profile is currently in a conversation, this method will return the identifier of that conversation.
     *
     * @param profile the profile to get the conversation identifier for
     * @return the identifier of the conversation the profile is currently in, or an empty optional
     * @since 3.0.0
     */
    Optional<ConversationIdentifier> getActive(Profile profile);
}
