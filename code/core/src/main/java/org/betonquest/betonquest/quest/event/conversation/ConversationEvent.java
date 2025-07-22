package org.betonquest.betonquest.quest.event.conversation;

import org.apache.commons.lang3.tuple.Pair;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.online.OnlineEvent;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.conversation.Conversation;
import org.betonquest.betonquest.id.ConversationID;
import org.betonquest.betonquest.instruction.variable.Variable;

/**
 * Starts a conversation.
 */
public class ConversationEvent implements OnlineEvent {
    /**
     * Logger factory to create a logger for the events.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * The {@link PluginMessage} instance.
     */
    private final PluginMessage pluginMessage;

    /**
     * The conversation to start.
     */
    private final Variable<Pair<ConversationID, String>> conversation;

    /**
     * Creates a new ConversationEvent.
     *
     * @param loggerFactory the logger factory to create a logger for the events
     * @param pluginMessage the {@link PluginMessage} instance
     * @param conversation  the conversation and option to start as a pair
     */
    public ConversationEvent(final BetonQuestLoggerFactory loggerFactory, final PluginMessage pluginMessage,
                             final Variable<Pair<ConversationID, String>> conversation) {
        this.loggerFactory = loggerFactory;
        this.pluginMessage = pluginMessage;
        this.conversation = conversation;
    }

    @Override
    public void execute(final OnlineProfile profile) throws QuestException {
        final Pair<ConversationID, String> conversation = this.conversation.getValue(profile);
        new Conversation(loggerFactory.create(Conversation.class), pluginMessage, profile, conversation.getKey(),
                profile.getPlayer().getLocation(), conversation.getValue());
    }
}
