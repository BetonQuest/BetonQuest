package org.betonquest.betonquest.compatibility.itemsadder.action;

import dev.lone.itemsadder.api.CustomBlock;
import dev.lone.itemsadder.api.CustomStack;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.bukkit.Location;

/**
 * An action that places an ItemsAdder custom block at a specific location.
 *
 * <p>This action resolves the custom stack and target location from the profile,
 * validates that the item is indeed a block, and then uses the ItemsAdder API
 * to place it in the world.</p>
 */
public class ItemsAdderSetBlockAction implements PlayerAction {

    /**
     * The ItemsAdder custom stack argument representing the block to place.
     */
    private final Argument<CustomStack> customStackArgument;

    /**
     * The target location for the block placement.
     */
    private final Argument<Location> locationArgument;

    /**
     * Creates a new ItemsAdderSetBlockAction.
     *
     * @param customStackArgument the argument for the ItemsAdder custom stack
     * @param locationArgument    the argument for the target location
     */
    public ItemsAdderSetBlockAction(final Argument<CustomStack> customStackArgument, final Argument<Location> locationArgument) {
        this.customStackArgument = customStackArgument;
        this.locationArgument = locationArgument;
    }

    @Override
    public void execute(final Profile profile) throws QuestException {
        final Location location = locationArgument.getValue(profile);
        final CustomStack customStack = customStackArgument.getValue(profile);
        if (!customStack.isBlock()) {
            throw new QuestException("ItemsAdder item is not a block: " + customStack);
        }
        CustomBlock.place(customStack.getNamespacedID(), location);
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }

}
