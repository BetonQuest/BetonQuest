package org.betonquest.betonquest.compatibility.itemsadder.action;

import dev.lone.itemsadder.api.CustomBlock;
import dev.lone.itemsadder.api.CustomStack;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.bukkit.Location;

/**
 * Sets a {@link CustomBlock} at a {@link Location}.
 */
public class IASetBlockAction implements PlayerAction {

    /**
     * Item of block to set.
     */
    private final Argument<CustomStack> itemID;

    /**
     * Location to set the block at.
     */
    private final Argument<Location> location;

    /**
     * Create a new ItemsAdder block set action.
     *
     * @param itemID   the item of the block to set
     * @param location the location to set the block at
     */
    public IASetBlockAction(final Argument<CustomStack> itemID, final Argument<Location> location) {
        this.itemID = itemID;
        this.location = location;
    }

    @Override
    public void execute(final Profile profile) throws QuestException {
        final Location loc = location.getValue(profile);
        final CustomStack customStack = itemID.getValue(profile);
        if (!customStack.isBlock()) {
            throw new QuestException("ItemsAdder Item is not a block: " + itemID);
        }
        CustomBlock.place(customStack.getNamespacedID(), loc);
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
