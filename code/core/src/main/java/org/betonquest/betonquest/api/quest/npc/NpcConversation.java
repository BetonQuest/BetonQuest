package org.betonquest.betonquest.api.quest.npc;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.Translations;
import org.betonquest.betonquest.api.identifier.ConversationIdentifier;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.service.action.ActionManager;
import org.betonquest.betonquest.api.service.condition.ConditionManager;
import org.betonquest.betonquest.api.service.identifier.Identifiers;
import org.betonquest.betonquest.conversation.Conversation;
import org.betonquest.betonquest.database.Saver;
import org.betonquest.betonquest.kernel.processor.feature.ConversationProcessor;
import org.bukkit.Location;

/**
 * Represents a conversation with Npc.
 *
 * @param <T> the original npc type
 */
public class NpcConversation<T> extends Conversation {

    /**
     * Npc used in this Conversation.
     */
    private final Npc<T> npc;

    /**
     * Starts a new conversation between player and npc at given location.
     *
     * @param log                   the logger that will be used for logging
     * @param translations          the {@link Translations} instance
     * @param onlineProfile         the profile of the player
     * @param conversationID        the ID of the conversation
     * @param center                the location where the conversation has been started
     * @param actionManager         the {@link ActionManager} instance
     * @param conditionManager      the {@link ConditionManager} instance
     * @param conversationProcessor the {@link ConversationProcessor} instance
     * @param identifiers           the {@link Identifiers} instance
     * @param saver                 the {@link Saver} instance
     * @param endCallable           the callable that removes the conversation from the active ones
     * @param npc                   the Npc used for this conversation
     * @throws QuestException when required conversation objects could not be created
     */
    @SuppressWarnings("PMD.ExcessiveParameterList")
    public NpcConversation(final BetonQuestLogger log, final Translations translations, final OnlineProfile onlineProfile,
                           final ConversationIdentifier conversationID, final Location center,
                           final ActionManager actionManager, final ConditionManager conditionManager,
                           final ConversationProcessor conversationProcessor, final Identifiers identifiers, final Saver saver,
                           final Runnable endCallable, final Npc<T> npc) throws QuestException {
        super(log, translations, onlineProfile, conversationID, actionManager, conditionManager, conversationProcessor, identifiers, saver, center, endCallable);
        this.npc = npc;
    }

    /**
     * This will return the Npc associated with this conversation.
     *
     * @return the BetonQuest Npc
     */
    public Npc<T> getNPC() {
        return npc;
    }
}
