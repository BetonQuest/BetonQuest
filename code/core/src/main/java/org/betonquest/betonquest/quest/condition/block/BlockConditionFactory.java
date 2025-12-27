package org.betonquest.betonquest.quest.condition.block;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.type.BlockSelector;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerlessConditionFactory;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableConditionAdapter;
import org.bukkit.Location;

/**
 * Factory to create test for block conditions from {@link Instruction}s.
 */
public class BlockConditionFactory implements PlayerConditionFactory, PlayerlessConditionFactory {

    /**
     * Create the test for block condition factory.
     */
    public BlockConditionFactory() {
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        return parseBlockCondition(instruction);
    }

    @Override
    public PlayerlessCondition parsePlayerless(final Instruction instruction) throws QuestException {
        return parseBlockCondition(instruction);
    }

    private NullableConditionAdapter parseBlockCondition(final Instruction instruction) throws QuestException {
        final Argument<Location> loc = instruction.location().get();
        final Argument<BlockSelector> selector = instruction.blockSelector().get();
        final FlagArgument<Boolean> exactMatch = instruction.bool().getFlag("exactMatch", false);
        return new NullableConditionAdapter(new BlockCondition(loc, selector, exactMatch));
    }
}
