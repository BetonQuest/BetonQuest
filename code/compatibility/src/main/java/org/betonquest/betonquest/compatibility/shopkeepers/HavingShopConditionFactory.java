package org.betonquest.betonquest.compatibility.shopkeepers;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;

/**
 * Factory to create {@link HavingShopCondition}s from {@link Instruction}s.
 */
public class HavingShopConditionFactory implements PlayerConditionFactory {

    /**
     * Create a new condition factory.
     */
    public HavingShopConditionFactory() {
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<Number> amount = instruction.number().get();
        return new HavingShopCondition(amount);
    }
}
