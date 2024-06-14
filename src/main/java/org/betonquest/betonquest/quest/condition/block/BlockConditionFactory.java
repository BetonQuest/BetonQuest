package org.betonquest.betonquest.quest.condition.block;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.condition.Condition;
import org.betonquest.betonquest.api.quest.condition.ConditionFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.condition.PrimaryServerThreadCondition;
import org.betonquest.betonquest.utils.BlockSelector;
import org.betonquest.betonquest.utils.location.CompoundLocation;

/**
 * Factory to create test for block conditions from {@link Instruction}s.
 */
public class BlockConditionFactory implements ConditionFactory {
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
    public Condition parse(final Instruction instruction) throws InstructionParseException {
        final CompoundLocation loc = instruction.getLocation();
        final BlockSelector selector = instruction.getBlockSelector();
        final boolean exactMatch = instruction.hasArgument("exactMatch");
        return new PrimaryServerThreadCondition(new BlockCondition(loc, selector, exactMatch), data);
    }
}
