package org.betonquest.betonquest.quest.event.conversation;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.online.OnlineEvent;
import org.betonquest.betonquest.conversation.Conversation;
import org.betonquest.betonquest.id.ConversationID;
import org.jetbrains.annotations.Nullable;

/**
 * Starts a conversation.
 */
public class ConversationEvent implements OnlineEvent {
    /**
     * The {@link BetonQuestLoggerFactory} to use for creating {@link BetonQuestLogger} instances.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * The conversation to start.
     */
    private final ConversationID conversation;

    /**
     * The optional NPC option to start at.
     */
    @Nullable
    private final String startOption;

    /**
     * Creates a new ConversationEvent.
     *
     * @param loggerFactory loggerFactory to use
     * @param conversation  the conversation to start
     * @param startOption   name of the option which the conversation should start at
     */
    public ConversationEvent(final BetonQuestLoggerFactory loggerFactory, final ConversationID conversation, @Nullable final String startOption) {
        this.loggerFactory = loggerFactory;
        this.conversation = conversation;
        this.startOption = startOption;
    }

    @Override
    public void execute(final OnlineProfile profile) throws QuestException {
        new Conversation(loggerFactory.create(Conversation.class), profile, conversation, profile.getPlayer().getLocation(), startOption);
    }
}
