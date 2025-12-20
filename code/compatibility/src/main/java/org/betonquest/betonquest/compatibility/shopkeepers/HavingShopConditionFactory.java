package org.betonquest.betonquest.compatibility.shopkeepers;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.DefaultInstruction;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.thread.PrimaryServerThreadPlayerCondition;

/**
 * Factory to create {@link HavingShopCondition}s from {@link DefaultInstruction}s.
 */
public class HavingShopConditionFactory implements PlayerConditionFactory {

    /**
     * Data for primary server thread access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create a new condition factory.
     *
     * @param data the data for primary server thread access
     */
    public HavingShopConditionFactory(final PrimaryServerThreadData data) {
        this.data = data;
    }

    @Override
    public PlayerCondition parsePlayer(final DefaultInstruction instruction) throws QuestException {
        final Variable<Number> amount = instruction.get(Argument.NUMBER);
        return new PrimaryServerThreadPlayerCondition(new HavingShopCondition(amount), data);
    }
}
