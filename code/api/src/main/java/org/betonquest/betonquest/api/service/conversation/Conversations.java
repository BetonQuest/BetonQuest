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
 * <p>
 * Conversations are the fundamental concept underlying almost all interactions in BetonQuest.
 * Each conversation is uniquely identified by a {@link ConversationIdentifier}, which consists of the
 * user-defined name in the configuration and the {@link QuestPackage} it belongs to.
 *
 * @since 3.0.0
 */
public interface Conversations {

    /**
     * Evaluates whether a conversation can be started by a given {@link Profile}.
     * <p>
     * A conversation can only be started if at least one starting option defined for it
     * has its conditions met.
     *
     * @param profile                the profile attempting to start the conversation
     * @param conversationIdentifier the identifier of the conversation to start
     * @return {@code true} if the conversation can be started, {@code false} otherwise
     * @throws QuestException if the conversation is not available
     * @since 3.0.0
     */
    boolean canStart(Profile profile, ConversationIdentifier conversationIdentifier) throws QuestException;

    /**
     * Forcefully starts a conversation for a given {@link OnlineProfile} at a given {@link Location}
     * and with an optional starting option.
     * <p>
     * Starting a conversation with a non-null starting option ignores starting conditions
     * to start the conversation immediately. If no starting option is provided,
     * the conversation starts based on its starting options and their conditions.
     * <p>
     * The location is used to keep the player at that specific position.
     * The precise behavior may differ based on the configured conversationIO.
     * <p>
     * This method triggers a {@code PlayerConversationStartEvent}, which can be canceled.
     *
     * @param profile                the profile to start the conversation for
     * @param conversationIdentifier the identifier of the conversation to start
     * @param location               the location where the player should stay
     * @param startingOption         the optional starting option to start with
     * @since 3.0.0
     */
    void start(OnlineProfile profile, ConversationIdentifier conversationIdentifier, Location location, @Nullable String startingOption);

    /**
     * Determines whether a {@link Profile} is currently engaged in a conversation.
     *
     * @param profile the profile that might be in a conversation
     * @return {@code true} if the profile is currently in a conversation, {@code false} otherwise
     * @since 3.0.0
     */
    boolean hasActive(Profile profile);

    /**
     * Cancels the active conversation for the given profile.
     * Does nothing if the profile is not currently in a conversation.
     *
     * @param profile the profile to cancel the conversation for
     * @since 3.0.0
     * @deprecated for removal in {@code 4.0.0}, use {@link #cancel(OnlineProfile, boolean)} instead
     */
    @Deprecated(forRemoval = true, since = "3.3.0")
    default void cancel(final OnlineProfile profile) {
        cancel(profile, false);
    }

    /**
     * Cancels the active conversation for the given profile with an option to skip the interceptor delay.
     * <p>
     * If the profile is not in a conversation but a pending interceptor is active,
     * the interceptor is canceled immediately. Otherwise, nothing happens.
     *
     * @param profile   the profile to cancel the conversation for
     * @param skipDelay whether the interceptor delay should be skipped
     * @since 3.3.0
     */
    void cancel(OnlineProfile profile, boolean skipDelay);

    /**
     * Returns the display name of the quester for the given profile in the active conversation.
     *
     * @param profile the profile to get the quester name for
     * @return an {@link Optional} containing the display name, or an empty {@link Optional} if no conversation is active
     * @since 3.0.0
     */
    Optional<Component> getActiveQuesterName(Profile profile);

    /**
     * Sends a bypass message to the given profile, which is displayed to the player
     * even if they are currently engaged in a conversation.
     * <p>
     * If the profile is not in a conversation, the message is sent normally.
     *
     * @param profile the profile to send the bypass message to
     * @param message the message to send
     * @since 3.0.0
     */
    void sendBypassMessage(OnlineProfile profile, Component message);

    /**
     * Returns the identifier of the conversation the given profile is currently in.
     *
     * @param profile the profile to get the conversation identifier for
     * @return an {@link Optional} containing the identifier, or an empty {@link Optional} if not in a conversation
     * @since 3.0.0
     */
    Optional<ConversationIdentifier> getActive(Profile profile);
}
