package org.betonquest.betonquest.quest.condition.item;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.type.ItemWrapper;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.condition.OnlineConditionAdapter;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.data.PlayerDataStorage;

import java.util.List;

/**
 * Factory for {@link ItemCondition}s.
 */
public class ItemConditionFactory implements PlayerConditionFactory {

    /**
     * Logger factory to create a logger for the conditions.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Storage for player data.
     */
    private final PlayerDataStorage dataStorage;

    /**
     * Create the item factory.
     *
     * @param loggerFactory the logger factory to create a logger for the conditions
     * @param dataStorage   the storage providing player data
     */
    public ItemConditionFactory(final BetonQuestLoggerFactory loggerFactory, final PlayerDataStorage dataStorage) {
        this.loggerFactory = loggerFactory;
        this.dataStorage = dataStorage;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<List<ItemWrapper>> items = instruction.item().list().get();
        final BetonQuestLogger log = loggerFactory.create(ItemCondition.class);
        return new OnlineConditionAdapter(new ItemCondition(items, dataStorage), log, instruction.getPackage());
    }
}
