package org.betonquest.betonquest.quest.event.chest;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEvent;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.Nullable;

/**
 * Abstract class for chest events.
 */
public abstract class AbstractChestEvent implements NullableEvent {

    /**
     * The location of the chest.
     */
    private final Argument<Location> location;

    /**
     * Creates a new chest clear event.
     *
     * @param location the location of the chest
     */
    public AbstractChestEvent(final Argument<Location> location) {
        this.location = location;
    }

    /**
     * Returns the chest at the specified location.
     *
     * @param profile the profile of the player
     * @return the chest {@link InventoryHolder} at the specified location
     * @throws QuestException if there is no chest at the specified location
     */
    protected InventoryHolder getChest(@Nullable final Profile profile) throws QuestException {
        final Block block = location.getValue(profile).getBlock();
        try {
            return (InventoryHolder) block.getState();
        } catch (final ClassCastException e) {
            throw new QuestException("No chest found at location: X"
                    + block.getX() + " Y" + block.getY() + " Z" + block.getZ(), e);
        }
    }
}
