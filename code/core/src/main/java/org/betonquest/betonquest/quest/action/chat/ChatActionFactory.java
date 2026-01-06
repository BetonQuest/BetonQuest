package org.betonquest.betonquest.quest.action.chat;

import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.api.quest.action.online.OnlineActionAdapter;

/**
 * The chat event factory.
 */
public class ChatActionFactory implements PlayerActionFactory {

    /**
     * Logger factory to create a logger for the events.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Create the chat event factory.
     *
     * @param loggerFactory the logger factory to create a logger for the events
     */
    public ChatActionFactory(final BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) {
        final String[] messages = String.join(" ", instruction.getValueParts()).split("\\|");
        return new OnlineActionAdapter(new ChatAction(messages),
                loggerFactory.create(ChatAction.class),
                instruction.getPackage());
    }
}
