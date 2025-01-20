package org.betonquest.betonquest.quest.condition.block;

import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableCondition;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.betonquest.betonquest.util.BlockSelector;
import org.bukkit.block.Block;
import org.jetbrains.annotations.Nullable;

/**
 * Checks block at specified location against specified {@link BlockSelector}.
 */
public class BlockCondition implements NullableCondition {
    /**
     * Location to test for the block.
     */
    private final VariableLocation loc;

    /**
     * Selector to validate the block.
     */
    private final BlockSelector selector;

    /**
     * If the selector match has to be exact.
     */
    private final boolean exactMatch;

    /**
     * Create a new TestForBlock condition.
     *
     * @param loc        the location to test for the block
     * @param selector   the selector to validate the block
     * @param exactMatch if the selector match has to be exact
     */
    public BlockCondition(final VariableLocation loc, final BlockSelector selector, final boolean exactMatch) {
        this.loc = loc;
        this.selector = selector;
        this.exactMatch = exactMatch;
    }

    @Override
    public boolean check(@Nullable final Profile profile) throws QuestException {
        final Block block = loc.getValue(profile).getBlock();
        return selector.match(block, exactMatch);
    }
}
