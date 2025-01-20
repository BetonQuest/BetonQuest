package org.betonquest.betonquest.quest.condition.armor;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.online.OnlineConditionAdapter;
import org.betonquest.betonquest.id.ItemID;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.item.QuestItem;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.condition.PrimaryServerThreadPlayerCondition;

/**
 * Factory for {@link ArmorCondition}s from {@link Instruction}s.
 */
public class ArmorConditionFactory implements PlayerConditionFactory {

    /**
     * Logger factory to create a logger for conditions.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Data used for condition check on the primary server thread.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create the armor factory.
     *
     * @param loggerFactory the logger factory
     * @param data          the data used for checking the condition on the main thread
     */
    public ArmorConditionFactory(final BetonQuestLoggerFactory loggerFactory, final PrimaryServerThreadData data) {
        this.loggerFactory = loggerFactory;
        this.data = data;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final QuestItem armorItem = new QuestItem(instruction.getID(ItemID::new));
        final BetonQuestLogger log = loggerFactory.create(ArmorCondition.class);
        return new PrimaryServerThreadPlayerCondition(
                new OnlineConditionAdapter(new ArmorCondition(armorItem), log, instruction.getPackage()), data);
    }
}
