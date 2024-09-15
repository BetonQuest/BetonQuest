package org.betonquest.betonquest.quest.event.npc;

import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.api.quest.event.online.OnlineEventAdapter;
import org.betonquest.betonquest.api.quest.npc.feature.NpcHider;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadEvent;

/**
 * Factory to create {@link UpdateVisibilityNowEvent}s from {@link Instruction}s.
 */
public class UpdateVisibilityNowEventFactory implements EventFactory {
    /**
     * Hider to update visibility.
     */
    private final NpcHider npcHider;

    /**
     * Logger factory to create a new custom logger.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Data for primary server thread access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create the Npc visibility update event factory.
     *
     * @param npcHider      the hider where to update the visibility
     * @param loggerFactory the logger factory to create new custom logger
     * @param data          the data for primary server thread access
     */
    public UpdateVisibilityNowEventFactory(final NpcHider npcHider, final BetonQuestLoggerFactory loggerFactory, final PrimaryServerThreadData data) {
        this.npcHider = npcHider;
        this.loggerFactory = loggerFactory;
        this.data = data;
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws QuestException {
        return new PrimaryServerThreadEvent(new OnlineEventAdapter(
                new UpdateVisibilityNowEvent(npcHider),
                loggerFactory.create(UpdateVisibilityNowEvent.class),
                instruction.getPackage()
        ), data);
    }
}
