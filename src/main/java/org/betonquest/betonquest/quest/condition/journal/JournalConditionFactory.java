package org.betonquest.betonquest.quest.condition.journal;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.online.OnlineConditionAdapter;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.modules.data.PlayerDataStorage;
import org.betonquest.betonquest.utils.Utils;

/**
 * Factory for {@link JournalCondition}s.
 */
public class JournalConditionFactory implements PlayerConditionFactory {

    /**
     * Storage for player data.
     */
    private final PlayerDataStorage dataStorage;

    /**
     * Logger factory to create a logger for conditions.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Create the journal condition factory.
     *
     * @param dataStorage   the storage providing player data
     * @param loggerFactory the logger factory
     */
    public JournalConditionFactory(final PlayerDataStorage dataStorage, final BetonQuestLoggerFactory loggerFactory) {
        this.dataStorage = dataStorage;
        this.loggerFactory = loggerFactory;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final String targetPointer = Utils.addPackage(instruction.getPackage(), instruction.next());
        final BetonQuestLogger log = loggerFactory.create(JournalCondition.class);
        return new OnlineConditionAdapter(new JournalCondition(dataStorage, targetPointer), log, instruction.getPackage());
    }
}
