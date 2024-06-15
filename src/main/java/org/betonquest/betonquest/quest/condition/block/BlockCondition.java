package org.betonquest.betonquest.quest.condition.block;

import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.condition.Condition;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.BlockSelector;
import org.betonquest.betonquest.utils.location.CompoundLocation;
import org.bukkit.block.Block;
import org.jetbrains.annotations.Nullable;

/**
 * Checks block at specified location against specified {@link BlockSelector}.
 */
public class BlockCondition implements Condition {
    /**
     * Location to test for the block.
     */
    private final CompoundLocation loc;

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
    public BlockCondition(final CompoundLocation loc, final BlockSelector selector, final boolean exactMatch) {
        this.loc = loc;
        this.selector = selector;
        this.exactMatch = exactMatch;
    }

    @Override
    public boolean check(final OnlineProfile profile) throws QuestRuntimeException {
        return check((Profile) profile);
    }

    @Override
    public boolean check(@Nullable final Profile profile) throws QuestRuntimeException {
        final Block block = loc.getLocation(profile).getBlock();
        return selector.match(block, exactMatch);
    }
}
