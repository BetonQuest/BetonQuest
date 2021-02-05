package org.betonquest.betonquest.conditions;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.BlockSelector;
import org.betonquest.betonquest.utils.location.CompoundLocation;
import org.bukkit.block.Block;

/**
 * Checks block at specified location against specified {@link BlockSelector}
 */
@SuppressWarnings("PMD.CommentRequired")
public class TestForBlockCondition extends Condition {

    private final CompoundLocation loc;
    private final BlockSelector selector;
    private final boolean exactMatch;

    public TestForBlockCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        staticness = true;
        persistent = true;
        loc = instruction.getLocation();
        selector = instruction.getBlockSelector();
        exactMatch = instruction.hasArgument("exactMatch");
    }

    @Override
    protected Boolean execute(final String playerID) throws QuestRuntimeException {
        final Block block = loc.getLocation(playerID).getBlock();
        return selector.match(block, exactMatch);
    }

}
