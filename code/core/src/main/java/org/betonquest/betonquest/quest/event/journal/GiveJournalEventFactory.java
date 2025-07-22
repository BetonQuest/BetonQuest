package org.betonquest.betonquest.quest.event.journal;

import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.online.OnlineEventAdapter;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadEvent;

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
     * Plugin Message instance to create the journal.
     */
    private final PluginMessage pluginMessage;

    /**
     * Data for primary server thread access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create the give journal event factory.
     *
     * @param loggerFactory the logger factory to create a logger for the events
     * @param pluginMessage the plugin message to create the journal
     * @param dataStorage   the storage providing player data
     * @param data          the data for primary server thread access
     */
    public GiveJournalEventFactory(final BetonQuestLoggerFactory loggerFactory, final PlayerDataStorage dataStorage, final PluginMessage pluginMessage, final PrimaryServerThreadData data) {
        this.loggerFactory = loggerFactory;
        this.dataStorage = dataStorage;
        this.pluginMessage = pluginMessage;
        this.data = data;
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        return new PrimaryServerThreadEvent(new OnlineEventAdapter(
                new GiveJournalEvent(dataStorage::get, pluginMessage),
                loggerFactory.create(GiveJournalEvent.class),
                instruction.getPackage()
        ), data);
    }
}
