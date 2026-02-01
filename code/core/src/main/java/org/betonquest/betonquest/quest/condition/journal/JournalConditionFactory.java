package org.betonquest.betonquest.quest.condition.journal;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.JournalEntryIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.condition.OnlineConditionAdapter;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.data.PlayerDataStorage;

/**
 * Factory for {@link JournalCondition}s.
 */
public class JournalConditionFactory implements PlayerConditionFactory {

    /**
     * Storage for player data.
     */
    private final PlayerDataStorage dataStorage;

    /**
     * Logger factory to create a logger for the conditions.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Create the journal condition factory.
     *
     * @param dataStorage   the storage providing player data
     * @param loggerFactory the logger factory to create a logger for the conditions
     */
    public JournalConditionFactory(final PlayerDataStorage dataStorage, final BetonQuestLoggerFactory loggerFactory) {
        this.dataStorage = dataStorage;
        this.loggerFactory = loggerFactory;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<JournalEntryIdentifier> entryID = instruction.identifier(JournalEntryIdentifier.class).get();
        final BetonQuestLogger log = loggerFactory.create(JournalCondition.class);
        return new OnlineConditionAdapter(new JournalCondition(dataStorage, entryID), log, instruction.getPackage());
    }
}
