package org.betonquest.betonquest.quest.condition.block;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerlessConditionFactory;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableConditionAdapter;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.condition.PrimaryServerThreadPlayerCondition;
import org.betonquest.betonquest.quest.condition.PrimaryServerThreadPlayerlessCondition;
import org.betonquest.betonquest.utils.BlockSelector;

/**
 * Factory to create test for block conditions from {@link Instruction}s.
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
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        return new PrimaryServerThreadPlayerCondition(parseBlockCondition(instruction), data);
    }

    @Override
    public PlayerlessCondition parsePlayerless(final Instruction instruction) throws QuestException {
        return new PrimaryServerThreadPlayerlessCondition(parseBlockCondition(instruction), data);
    }

    private NullableConditionAdapter parseBlockCondition(final Instruction instruction) throws QuestException {
        final VariableLocation loc = instruction.getLocation();
        final BlockSelector selector = instruction.getBlockSelector();
        final boolean exactMatch = instruction.hasArgument("exactMatch");
        return new NullableConditionAdapter(new BlockCondition(loc, selector, exactMatch));
    }
}
