package org.betonquest.betonquest.quest.action.journal;

import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.action.OnlineActionAdapter;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.data.PlayerDataStorage;

/**
 * Creates a new GiveJournalAction from an {@link Instruction}.
 */
public class GiveJournalActionFactory implements PlayerActionFactory {

    /**
     * Logger factory to create a logger for the actions.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Storage for player data.
     */
    private final PlayerDataStorage dataStorage;

    /**
     * Create the give journal action factory.
     *
     * @param loggerFactory the logger factory to create a logger for the actions
     * @param dataStorage   the storage providing player data
     */
    public GiveJournalActionFactory(final BetonQuestLoggerFactory loggerFactory, final PlayerDataStorage dataStorage) {
        this.loggerFactory = loggerFactory;
        this.dataStorage = dataStorage;
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) {
        return new OnlineActionAdapter(new GiveJournalAction(dataStorage::get),
                loggerFactory.create(GiveJournalAction.class),
                instruction.getPackage());
    }
}
