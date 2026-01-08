package org.betonquest.betonquest.compatibility.itemsadder.action;

import dev.lone.itemsadder.api.CustomFurniture;
import dev.lone.itemsadder.api.CustomStack;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.bukkit.Location;

/**
 * An action that spawns an ItemsAdder custom furniture at a specific location.
 *
 * <p>This action resolves the custom stack and target location from the profile,
 * validates that the item can be treated as furniture/block, and then uses
 * the ItemsAdder API to spawn the furniture entity at the given block location.</p>
 */
public class ItemsAdderSetFurnitureAction implements PlayerAction {

    /**
     * The ItemsAdder custom stack argument representing the furniture to spawn.
     */
    private final Argument<CustomStack> customStackArgument;

    /**
     * The target location for the furniture placement.
     */
    private final Argument<Location> locationArgument;

    /**
     * Creates a new ItemsAdderSetFurnitureAction.
     *
     * @param customStackArgument the argument for the ItemsAdder custom stack
     * @param locationArgument    the argument for the target location
     */
    public ItemsAdderSetFurnitureAction(final Argument<CustomStack> customStackArgument, final Argument<Location> locationArgument) {
        this.customStackArgument = customStackArgument;
        this.locationArgument = locationArgument;
    }

    @Override
    public void execute(final Profile profile) throws QuestException {
        final Location location = locationArgument.getValue(profile);
        final CustomStack customStack = customStackArgument.getValue(profile);

        if (!customStack.isBlock()) {
            throw new QuestException("ItemsAdder item is not a furniture: " + customStack);
        }

        CustomFurniture.spawn(customStack.getNamespacedID(), location.getBlock());
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
