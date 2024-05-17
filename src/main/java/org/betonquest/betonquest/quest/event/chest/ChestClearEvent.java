package org.betonquest.betonquest.quest.event.chest;

import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.event.HybridEvent;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.location.CompoundLocation;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.Nullable;

/**
 * Clears a specified chest from all items inside.
 */
public class ChestClearEvent extends AbstractChestEvent implements HybridEvent {

    /**
     * Creates a new chest clear event.
     *
     * @param compoundLocation the location of the chest
     */
    public ChestClearEvent(final CompoundLocation compoundLocation) {
        super(compoundLocation);
    }

    @Override
    public void execute(@Nullable final Profile profile) throws QuestRuntimeException {
        try {
            final InventoryHolder chest = getChest(profile);
            chest.getInventory().clear();
        } catch (final QuestRuntimeException e) {
            throw new QuestRuntimeException("Trying to clear chest. " + e.getMessage(), e);
        }
    }
}
