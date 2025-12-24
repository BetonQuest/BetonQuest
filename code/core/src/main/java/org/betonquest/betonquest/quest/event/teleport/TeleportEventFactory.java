package org.betonquest.betonquest.quest.event.teleport;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.feature.ConversationApi;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.online.OnlineEventAdapter;
import org.betonquest.betonquest.api.quest.event.thread.PrimaryServerThreadEvent;
import org.bukkit.Location;

/**
 * Factory to create teleport events from {@link Instruction}s.
 */
public class TeleportEventFactory implements PlayerEventFactory {

    /**
     * Logger factory to create a logger for the events.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Conversation API.
     */
    private final ConversationApi conversationApi;

    /**
     * Data for primary server thread access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create the teleport event factory.
     *
     * @param loggerFactory   the logger factory to create a logger for the events
     * @param conversationApi the Conversation API
     * @param data            the data for primary server thread access
     */
    public TeleportEventFactory(final BetonQuestLoggerFactory loggerFactory, final ConversationApi conversationApi, final PrimaryServerThreadData data) {
        this.loggerFactory = loggerFactory;
        this.conversationApi = conversationApi;
        this.data = data;
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        final Variable<Location> location = instruction.location().get();
        return new PrimaryServerThreadEvent(new OnlineEventAdapter(
                new TeleportEvent(conversationApi, location),
                loggerFactory.create(TeleportEvent.class),
                instruction.getPackage()
        ), data);
    }
}
