package org.betonquest.betonquest.quest.condition.looking;

import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.online.OnlineCondition;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.betonquest.betonquest.util.BlockSelector;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

/**
 * Checks if a player is looking at a specific block.
 */
public class LookingAtCondition implements OnlineCondition {

    /**
     * The location to check.
     */
    @Nullable
    private final Variable<Location> loc;

    /**
     * The block selector.
     */
    @Nullable
    private final Variable<BlockSelector> selector;

    /**
     * Whether the block must be an exact match.
     */
    private final boolean exactMatch;

    /**
     * Create a new looking at condition.
     *
     * @param loc the location to check
     */
    public LookingAtCondition(final Variable<Location> loc) {
        this.loc = loc;
        this.selector = null;
        this.exactMatch = false;
    }

    /**
     * Create a new looking at condition.
     *
     * @param selector   the block selector
     * @param exactMatch whether the block must be an exact match
     */
    public LookingAtCondition(final Variable<BlockSelector> selector, final boolean exactMatch) {
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
        return selector == null || selector.getValue(profile).match(lookingAt, exactMatch);
    }
}
