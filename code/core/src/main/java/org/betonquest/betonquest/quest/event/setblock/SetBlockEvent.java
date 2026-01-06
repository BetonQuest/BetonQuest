package org.betonquest.betonquest.quest.event.setblock;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.type.BlockSelector;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.action.nullable.NullableAction;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

/**
 * Sets a block at specified location.
 */
public class SetBlockEvent implements NullableAction {

    /**
     * The block selector.
     */
    private final Argument<BlockSelector> selector;

    /**
     * The location.
     */
    private final Argument<Location> location;

    /**
     * Whether to apply physics.
     */
    private final FlagArgument<Boolean> ignorePhysics;

    /**
     * Creates a new set block event.
     *
     * @param selector      the block selector
     * @param location      the location
     * @param ignorePhysics whether to apply physics
     */
    public SetBlockEvent(final Argument<BlockSelector> selector, final Argument<Location> location, final FlagArgument<Boolean> ignorePhysics) {
        this.selector = selector;
        this.location = location;
        this.ignorePhysics = ignorePhysics;
    }

    @Override
    public void execute(@Nullable final Profile profile) throws QuestException {
        final Location location = this.location.getValue(profile);
        final BlockSelector blockSelector = selector.getValue(profile);
        blockSelector.setToBlock(location.getBlock(), !ignorePhysics.getValue(profile).orElse(false));
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
