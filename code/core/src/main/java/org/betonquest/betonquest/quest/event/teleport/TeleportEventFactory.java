package org.betonquest.betonquest.quest.event.teleport;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.feature.ConversationApi;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.api.quest.action.online.OnlineActionAdapter;
import org.bukkit.Location;

/**
 * Factory to create teleport events from {@link Instruction}s.
 */
public class TeleportEventFactory implements PlayerActionFactory {

    /**
     * Logger factory to create a logger for the events.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Conversation API.
     */
    private final ConversationApi conversationApi;

    /**
     * Create the teleport event factory.
     *
     * @param loggerFactory   the logger factory to create a logger for the events
     * @param conversationApi the Conversation API
     */
    public TeleportEventFactory(final BetonQuestLoggerFactory loggerFactory, final ConversationApi conversationApi) {
        this.loggerFactory = loggerFactory;
        this.conversationApi = conversationApi;
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<Location> location = instruction.location().get();
        return new OnlineActionAdapter(new TeleportEvent(conversationApi, location),
                loggerFactory.create(TeleportEvent.class),
                instruction.getPackage());
    }
}
