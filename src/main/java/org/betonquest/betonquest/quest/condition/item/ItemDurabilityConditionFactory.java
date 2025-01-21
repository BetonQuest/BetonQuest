package org.betonquest.betonquest.quest.condition.item;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.online.OnlineConditionAdapter;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.condition.PrimaryServerThreadPlayerCondition;
import org.bukkit.inventory.EquipmentSlot;

/**
 * Factory for {@link ItemDurabilityCondition}s.
 */
public class ItemDurabilityConditionFactory implements PlayerConditionFactory {

    /**
     * Logger factory to create a logger for conditions.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Data used for condition check on the primary server thread.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create the item durability factory.
     *
     * @param loggerFactory the logger factory
     * @param data          the data used for checking the condition on the main thread
     */
    public ItemDurabilityConditionFactory(final BetonQuestLoggerFactory loggerFactory, final PrimaryServerThreadData data) {
        this.loggerFactory = loggerFactory;
        this.data = data;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final EquipmentSlot slot = instruction.getEnum(EquipmentSlot.class);
        final VariableNumber amount = instruction.get(VariableNumber::new);
        final boolean relative = instruction.hasArgument("relative");
        final BetonQuestLogger log = loggerFactory.create(ItemDurabilityCondition.class);
        return new PrimaryServerThreadPlayerCondition(
                new OnlineConditionAdapter(new ItemDurabilityCondition(slot, amount, relative), log, instruction.getPackage()), data
        );
    }
}
