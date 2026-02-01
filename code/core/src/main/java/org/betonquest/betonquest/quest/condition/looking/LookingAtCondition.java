package org.betonquest.betonquest.quest.condition.looking;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.type.BlockSelector;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.condition.OnlineCondition;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Checks if a player is looking at a specific block.
 */
public class LookingAtCondition implements OnlineCondition {

    /**
     * The location to check.
     */
    @Nullable
    private final Argument<Location> loc;

    /**
     * The block selector.
     */
    @Nullable
    private final Argument<BlockSelector> selector;

    /**
     * Whether the block must be an exact match.
     */
    private final FlagArgument<Boolean> exactMatch;

    /**
     * Create a new looking at condition.
     *
     * @param loc the location to check
     */
    public LookingAtCondition(final Argument<Location> loc) {
        this.loc = loc;
        this.selector = null;
        this.exactMatch = profile -> Optional.empty();
    }

    /**
     * Create a new looking at condition.
     *
     * @param selector   the block selector
     * @param exactMatch whether the block must be an exact match
     */
    public LookingAtCondition(final Argument<BlockSelector> selector, final FlagArgument<Boolean> exactMatch) {
        this.loc = null;
        this.selector = selector;
        this.exactMatch = exactMatch;
    }

    @Override
    public boolean check(final OnlineProfile profile) throws QuestException {
        final Player player = profile.getPlayer();
        final Block lookingAt = player.getTargetBlock(null, 6);
        if (loc != null) {
            final Location targetLocation = loc.getValue(profile);
            final Location actualLocation = lookingAt.getLocation();
            if (targetLocation.getBlockX() != actualLocation.getBlockX()
                    || targetLocation.getBlockY() != actualLocation.getBlockY()
                    || targetLocation.getBlockZ() != actualLocation.getBlockZ()) {
                return false;
            }
        }
        return selector == null || selector.getValue(profile).match(lookingAt, exactMatch.getValue(profile).orElse(false));
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
