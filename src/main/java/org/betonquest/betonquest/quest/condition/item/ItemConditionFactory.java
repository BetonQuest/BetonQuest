package org.betonquest.betonquest.quest.condition.item;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.online.OnlineConditionAdapter;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.Item;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.condition.PrimaryServerThreadPlayerCondition;

/**
 * Factory for {@link ItemCondition}s.
 */
public class ItemConditionFactory implements PlayerConditionFactory {

    /**
     * Logger factory to create a logger for conditions.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Data used for condition check on the primary server thread.
     */
    private final PrimaryServerThreadData data;

    /**
     * Storage for player data.
     */
    private final PlayerDataStorage dataStorage;

    /**
     * Create the item factory.
     *
     * @param loggerFactory the logger factory
     * @param data          the data used for checking the condition on the main thread
     * @param dataStorage   the storage providing player data
     */
    public ItemConditionFactory(final BetonQuestLoggerFactory loggerFactory, final PrimaryServerThreadData data, final PlayerDataStorage dataStorage) {
        this.loggerFactory = loggerFactory;
        this.data = data;
        this.dataStorage = dataStorage;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Item[] questItems = instruction.getItemList();
        final BetonQuestLogger log = loggerFactory.create(ItemCondition.class);
        return new PrimaryServerThreadPlayerCondition(
                new OnlineConditionAdapter(new ItemCondition(questItems, dataStorage), log, instruction.getPackage()), data
        );
    }
}
