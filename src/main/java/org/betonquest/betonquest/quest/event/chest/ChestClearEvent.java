package org.betonquest.betonquest.quest.event.chest;

import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.location.CompoundLocation;
import org.bukkit.block.Block;
import org.bukkit.inventory.InventoryHolder;

/**
 * Clears a specified chest from all items inside.
 */
public class ChestClearEvent implements Event {

    /**
     * The location of the chest.
     */
    private final CompoundLocation compoundLocation;

    /**
     * Creates a new chest clear event.
     *
     * @param compoundLocation the location of the chest
     */
    public ChestClearEvent(final CompoundLocation compoundLocation) {
        this.compoundLocation = compoundLocation;
    }

    @Override
    public void execute(final Profile profile) throws QuestRuntimeException {
        final Block block = compoundLocation.getLocation(profile).getBlock();
        final InventoryHolder chest;
        try {
            chest = (InventoryHolder) block.getState();
        } catch (final ClassCastException e) {
            throw new QuestRuntimeException("Trying to clears items in a chest, but there's no chest! Location: X"
                    + block.getX() + " Y" + block.getY() + " Z" + block.getZ(), e);
        }
        chest.getInventory().clear();
    }
}
