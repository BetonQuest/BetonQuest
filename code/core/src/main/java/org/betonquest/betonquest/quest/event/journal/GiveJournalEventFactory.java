package org.betonquest.betonquest.quest.event.journal;

import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.online.OnlineEventAdapter;
import org.betonquest.betonquest.data.PlayerDataStorage;

/**
 * Creates a new GiveJournalEvent from an {@link Instruction}.
 */
public class GiveJournalEventFactory implements PlayerEventFactory {

    /**
     * Logger factory to create a logger for the events.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Storage for player data.
     */
    private final PlayerDataStorage dataStorage;

    /**
     * Create the give journal event factory.
     *
     * @param loggerFactory the logger factory to create a logger for the events
     * @param dataStorage   the storage providing player data
     */
    public GiveJournalEventFactory(final BetonQuestLoggerFactory loggerFactory, final PlayerDataStorage dataStorage) {
        this.loggerFactory = loggerFactory;
        this.dataStorage = dataStorage;
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) {
        return new OnlineEventAdapter(new GiveJournalEvent(dataStorage::get),
                loggerFactory.create(GiveJournalEvent.class),
                instruction.getPackage());
    }
}
