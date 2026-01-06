package org.betonquest.betonquest.quest.action.teleport;

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
 * Factory to create teleport actions from {@link Instruction}s.
 */
public class TeleportActionFactory implements PlayerActionFactory {

    /**
     * Logger factory to create a logger for the actions.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Conversation API.
     */
    private final ConversationApi conversationApi;

    /**
     * Create the teleport action factory.
     *
     * @param loggerFactory   the logger factory to create a logger for the actions
     * @param conversationApi the Conversation API
     */
    public TeleportActionFactory(final BetonQuestLoggerFactory loggerFactory, final ConversationApi conversationApi) {
        this.loggerFactory = loggerFactory;
        this.conversationApi = conversationApi;
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<Location> location = instruction.location().get();
        return new OnlineActionAdapter(new TeleportAction(conversationApi, location),
                loggerFactory.create(TeleportAction.class),
                instruction.getPackage());
    }
}
