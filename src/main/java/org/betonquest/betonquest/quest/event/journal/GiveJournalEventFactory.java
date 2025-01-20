package org.betonquest.betonquest.quest.event.journal;

import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.api.quest.event.online.OnlineEventAdapter;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.instruction.Instruction;
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
     * Storage for player data.
     */
    private final PlayerDataStorage dataStorage;

    /**
     * Data for primary server thread access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create the give journal event factory.
     *
     * @param loggerFactory logger factory to use
     * @param dataStorage   the storage providing player data
     * @param data          the data for primary server thread access
     */
    public GiveJournalEventFactory(final BetonQuestLoggerFactory loggerFactory, final PlayerDataStorage dataStorage, final PrimaryServerThreadData data) {
        this.loggerFactory = loggerFactory;
        this.dataStorage = dataStorage;
        this.data = data;
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws QuestException {
        return new PrimaryServerThreadEvent(new OnlineEventAdapter(
                new GiveJournalEvent(dataStorage::get),
                loggerFactory.create(GiveJournalEvent.class),
                instruction.getPackage()
        ), data);
    }
}
