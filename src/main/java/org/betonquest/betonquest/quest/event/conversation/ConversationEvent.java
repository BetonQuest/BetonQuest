package org.betonquest.betonquest.quest.event.conversation;

import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.online.OnlineEvent;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.conversation.Conversation;
import org.betonquest.betonquest.id.ConversationID;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.jetbrains.annotations.Nullable;

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
    private final Variable<ConversationID> conversation;

    /**
     * The optional NPC option to start at.
     */
    @Nullable
    private final Variable<String> startOption;

    /**
     * Creates a new ConversationEvent.
     *
     * @param loggerFactory the logger factory to create a logger for the events
     * @param pluginMessage the {@link PluginMessage} instance
     * @param conversation  the conversation to start
     * @param startOption   name of the option which the conversation should start at
     */
    public ConversationEvent(final BetonQuestLoggerFactory loggerFactory, final PluginMessage pluginMessage,
                             final Variable<ConversationID> conversation, @Nullable final Variable<String> startOption) {
        this.loggerFactory = loggerFactory;
        this.pluginMessage = pluginMessage;
        this.conversation = conversation;
        this.startOption = startOption;
    }

    @Override
    public void execute(final OnlineProfile profile) throws QuestException {
        new Conversation(loggerFactory.create(Conversation.class), pluginMessage, profile, conversation.getValue(profile),
                profile.getPlayer().getLocation(), startOption == null ? null : startOption.getValue(profile));
    }
}
