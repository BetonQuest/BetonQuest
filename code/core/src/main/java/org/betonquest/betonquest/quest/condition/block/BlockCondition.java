package org.betonquest.betonquest.quest.condition.block;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.type.BlockSelector;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableCondition;
import org.betonquest.betonquest.util.DefaultBlockSelector;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.jetbrains.annotations.Nullable;

/**
 * Checks block at specified location against specified {@link DefaultBlockSelector}.
 */
public class BlockCondition implements NullableCondition {

    /**
     * The location to test for the block.
     */
    private final Argument<Location> loc;

    /**
     * The selector to validate the block.
     */
    private final Argument<BlockSelector> selector;

    /**
     * If the selector match has to be exact.
     */
    private final FlagArgument<Boolean> exactMatch;

    /**
     * Create a new TestForBlock condition.
     *
     * @param loc        the location to test for the block
     * @param selector   the selector to validate the block
     * @param exactMatch if the selector match has to be exact
     */
    public BlockCondition(final Argument<Location> loc, final Argument<BlockSelector> selector, final FlagArgument<Boolean> exactMatch) {
        this.loc = loc;
        this.selector = selector;
        this.exactMatch = exactMatch;
    }

    @Override
    public boolean check(@Nullable final Profile profile) throws QuestException {
        final Block block = loc.getValue(profile).getBlock();
        final BlockSelector blockSelector = selector.getValue(profile);
        return blockSelector.match(block, exactMatch.getValue(profile).orElse(false));
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
