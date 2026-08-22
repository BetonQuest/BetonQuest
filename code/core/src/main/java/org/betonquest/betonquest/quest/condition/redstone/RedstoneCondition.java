package org.betonquest.betonquest.quest.condition.redstone;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.condition.NullableCondition;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

/**
 * Condition to check if a block is powered.
 */
public class RedstoneCondition implements NullableCondition {

    /**
     * The location of the block to check for redstone power.
     */
    private final Argument<Location> location;

    /**
     * Whether the block is powered and how strongly.
     */
    private final Argument<Number> powered;

    /**
     * Creates a new redstone condition.
     *
     * @param location the location of the block
     * @param powered  whether the block is powered
     */
    public RedstoneCondition(final Argument<Location> location, final Argument<Number> powered) {
        this.location = location;
        this.powered = powered;
    }

    @Override
    public boolean check(@Nullable final Profile profile) throws QuestException {
        final Location value = location.getValue(profile);
        final int blockPower = value.getBlock().getBlockPower();
        final int requiredPower = powered.getValue(profile).intValue();
        return blockPower >= requiredPower;
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
