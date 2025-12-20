package org.betonquest.betonquest.quest.condition.block;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.DefaultInstruction;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerlessConditionFactory;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableConditionAdapter;
import org.betonquest.betonquest.api.quest.condition.thread.PrimaryServerThreadPlayerCondition;
import org.betonquest.betonquest.api.quest.condition.thread.PrimaryServerThreadPlayerlessCondition;
import org.betonquest.betonquest.util.BlockSelector;
import org.bukkit.Location;

/**
 * Factory to create test for block conditions from {@link DefaultInstruction}s.
 */
public class BlockConditionFactory implements PlayerConditionFactory, PlayerlessConditionFactory {

    /**
     * Data used for condition check on the primary server thread.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create the test for block condition factory.
     *
     * @param data the data used for checking the condition on the main thread
     */
    public BlockConditionFactory(final PrimaryServerThreadData data) {
        this.data = data;
    }

    @Override
    public PlayerCondition parsePlayer(final DefaultInstruction instruction) throws QuestException {
        return new PrimaryServerThreadPlayerCondition(parseBlockCondition(instruction), data);
    }

    @Override
    public PlayerlessCondition parsePlayerless(final DefaultInstruction instruction) throws QuestException {
        return new PrimaryServerThreadPlayerlessCondition(parseBlockCondition(instruction), data);
    }

    private NullableConditionAdapter parseBlockCondition(final DefaultInstruction instruction) throws QuestException {
        final Variable<Location> loc = instruction.get(Argument.LOCATION);
        final Variable<BlockSelector> selector = instruction.get(Argument.BLOCK_SELECTOR);
        final boolean exactMatch = instruction.hasArgument("exactMatch");
        return new NullableConditionAdapter(new BlockCondition(loc, selector, exactMatch));
    }
}
