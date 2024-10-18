package org.betonquest.betonquest.quest.event.chest;

import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEvent;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
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
    private final VariableLocation variableLocation;

    /**
     * Creates a new chest clear event.
     *
     * @param variableLocation the location of the chest
     */
    public AbstractChestEvent(final VariableLocation variableLocation) {
        this.variableLocation = variableLocation;
    }

    /**
     * Returns the chest at the specified location.
     *
     * @param profile the profile of the player
     * @return the chest {@link InventoryHolder} at the specified location
     * @throws QuestRuntimeException if there is no chest at the specified location
     */
    protected InventoryHolder getChest(@Nullable final Profile profile) throws QuestRuntimeException {
        final Block block = variableLocation.getValue(profile).getBlock();
        try {
            return (InventoryHolder) block.getState();
        } catch (final ClassCastException e) {
            throw new QuestRuntimeException("No chest found at location: X"
                    + block.getX() + " Y" + block.getY() + " Z" + block.getZ(), e);
        }
    }
}
