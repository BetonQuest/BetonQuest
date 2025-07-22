package org.betonquest.betonquest.quest.event.setblock;

import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEvent;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.betonquest.betonquest.util.BlockSelector;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

/**
 * Sets a block at specified location.
 */
public class SetBlockEvent implements NullableEvent {
    /**
     * The block selector.
     */
    private final Variable<BlockSelector> selector;

    /**
     * The location.
     */
    private final Variable<Location> variableLocation;

    /**
     * Whether to apply physics.
     */
    private final boolean applyPhysics;

    /**
     * Creates a new set block event.
     *
     * @param selector         the block selector
     * @param variableLocation the location
     * @param applyPhysics     whether to apply physics
     */
    public SetBlockEvent(final Variable<BlockSelector> selector, final Variable<Location> variableLocation, final boolean applyPhysics) {
        this.selector = selector;
        this.variableLocation = variableLocation;
        this.applyPhysics = applyPhysics;
    }

    @Override
    public void execute(@Nullable final Profile profile) throws QuestException {
        final Location location = variableLocation.getValue(profile);
        final BlockSelector blockSelector = selector.getValue(profile);
        blockSelector.setToBlock(location.getBlock(), applyPhysics);
    }
}
