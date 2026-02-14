package org.betonquest.betonquest.conversation;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.ActionIdentifier;
import org.betonquest.betonquest.api.identifier.ConversationIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.text.Text;
import org.betonquest.betonquest.conversation.interceptor.InterceptorFactory;

import java.util.List;

/**
 * The external used data.
 *
 * @param conversationID   The ID of the conversation.
 * @param quester          A map of the quester's name in different languages.
 * @param blockMovement    If true, the player will not be able to move during this conversation.
 * @param finalActions     All actions that will be executed when the conversation ends.
 * @param convIO           The conversation IO that should be used for this conversation.
 * @param interceptor      The interceptor that should be used for this conversation.
 * @param interceptorDelay The delay before the interceptor is ended after the conversation ends.
 * @param invincible       If true, the player will not be able to damage or be damaged by entities in conversation.
 */
public record ConversationPublicData(ConversationIdentifier conversationID, Text quester,
                                     Argument<Boolean> blockMovement,
                                     Argument<List<ActionIdentifier>> finalActions,
                                     Argument<ConversationIOFactory> convIO,
                                     Argument<InterceptorFactory> interceptor, Argument<Number> interceptorDelay,
                                     boolean invincible) {

    /**
     * Gets the quester's name in the specified language.
     * If the name is not translated the default language will be used.
     * <p>
     * Returns "Quester" in case of an exception.
     *
     * @param log     the logger used when the name could not be resolved
     * @param profile the profile to resolve the quester's name for
     * @return the quester's name in the specified language
     */
    public Component getQuester(final BetonQuestLogger log, final Profile profile) {
        try {
            return quester.asComponent(profile);
        } catch (final QuestException e) {
            log.warn("Could not get Quester's name! Using 'Quester' instead, reason: " + e.getMessage(), e);
            return Component.text("Quester");
        }
    }
}
