package org.betonquest.betonquest.quest.event.setblock;

import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.event.ComposedEvent;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.BlockSelector;
import org.betonquest.betonquest.utils.location.CompoundLocation;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

/**
 * Sets a block at specified location
 */
public class SetBlockEvent implements ComposedEvent {
    /**
     * The block selector
     */
    private final BlockSelector selector;

    /**
     * The location
     */
    private final CompoundLocation compoundLocation;

    /**
     * Whether to apply physics
     */
    private final boolean applyPhysics;

    /**
     * Creates a new set block event.
     *
     * @param selector         the block selector
     * @param compoundLocation the location
     * @param applyPhysics     whether to apply physics
     */
    public SetBlockEvent(final BlockSelector selector, final CompoundLocation compoundLocation, final boolean applyPhysics) {
        this.selector = selector;
        this.compoundLocation = compoundLocation;
        this.applyPhysics = applyPhysics;
    }

    @Override
    public void execute(@Nullable final Profile profile) throws QuestRuntimeException {
        final Location location = compoundLocation.getLocation(profile);
        selector.setToBlock(location.getBlock(), applyPhysics);
    }
}
