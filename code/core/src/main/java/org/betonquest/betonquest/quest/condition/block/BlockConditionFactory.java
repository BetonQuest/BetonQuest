package org.betonquest.betonquest.quest.condition.block;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.parser.DefaultArgumentParsers;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerlessConditionFactory;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableConditionAdapter;
import org.betonquest.betonquest.util.BlockSelector;
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
        final Variable<Location> loc = instruction.location().get();
        final Variable<BlockSelector> selector = instruction.parse(DefaultArgumentParsers.BLOCK_SELECTOR).get();
        final boolean exactMatch = instruction.hasArgument("exactMatch");
        return new NullableConditionAdapter(new BlockCondition(loc, selector, exactMatch));
    }
}
