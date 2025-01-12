package org.betonquest.betonquest.quest.event.journal;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.api.quest.event.online.OnlineEventAdapter;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadEvent;

/**
 * Creates a new GiveJournalEvent from an {@link Instruction}.
 */
public class GiveJournalEventFactory implements EventFactory {
    /**
     * Logger factory to create a logger for events.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * BetonQuest instance for fetching player data.
     */
    private final BetonQuest betonQuest;

    /**
     * Data for primary server thread access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create the give journal event factory.
     *
     * @param loggerFactory logger factory to use
     * @param betonQuest    BetonQuest instance to use
     * @param data          the data for primary server thread access
     */
    public GiveJournalEventFactory(final BetonQuestLoggerFactory loggerFactory, final BetonQuest betonQuest, final PrimaryServerThreadData data) {
        this.loggerFactory = loggerFactory;
        this.betonQuest = betonQuest;
        this.data = data;
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws QuestException {
        return new PrimaryServerThreadEvent(new OnlineEventAdapter(
                new GiveJournalEvent(betonQuest::getPlayerData),
                loggerFactory.create(GiveJournalEvent.class),
                instruction.getPackage()
        ), data);
    }
}
