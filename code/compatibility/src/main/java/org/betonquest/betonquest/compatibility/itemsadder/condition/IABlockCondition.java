package org.betonquest.betonquest.compatibility.itemsadder.condition;

import dev.lone.itemsadder.api.CustomBlock;
import dev.lone.itemsadder.api.CustomStack;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableCondition;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

/**
 * Checks for a {@link CustomBlock} at a {@link Location}.
 */
public class IABlockCondition implements NullableCondition {

    /**
     * Stack of the block.
     */
    private final Argument<CustomStack> itemID;

    /**
     * Location where the block is checked.
     */
    private final Argument<Location> location;

    /**
     * Create a new factory.
     *
     * @param itemID   the item of the block to check
     * @param location the location where the block is checked
     */
    public IABlockCondition(final Argument<CustomStack> itemID, final Argument<Location> location) {
        this.itemID = itemID;
        this.location = location;
    }

    @Override
    public boolean check(@Nullable final Profile profile) throws QuestException {
        final CustomBlock block = CustomBlock.byAlreadyPlaced(location.getValue(profile).getBlock());
        return block != null && block.matchNamespacedID(itemID.getValue(profile));
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
