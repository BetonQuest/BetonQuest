package org.betonquest.betonquest.compatibility.nexo.action;

import com.nexomc.nexo.api.NexoBlocks;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.bukkit.Location;

/**
 * An action that places a Nexo custom block at a specific location.
 *
 * <p>This action resolves the item ID and target location from the profile,
 * validates that the ID corresponds to a registered Nexo custom block,
 * and performs the placement.</p>
 */
public class NexoSetBlockAction implements PlayerAction {

    /**
     * The Nexo custom block item ID.
     */
    private final Argument<String> itemIdArgument;

    /**
     * The target location for the block placement.
     */
    private final Argument<Location> locationArgument;

    /**
     * Creates a new NexoSetBlockAction.
     *
     * @param itemIdArgument   the argument for the Nexo block item ID
     * @param locationArgument the argument for the target location
     */
    public NexoSetBlockAction(final Argument<String> itemIdArgument, final Argument<Location> locationArgument) {
        this.itemIdArgument = itemIdArgument;
        this.locationArgument = locationArgument;
    }

    @Override
    public void execute(final Profile profile) throws QuestException {
        final Location location = locationArgument.getValue(profile);
        final String itemId = itemIdArgument.getValue(profile);

        if (!NexoBlocks.isCustomBlock(itemId)) {
            throw new QuestException("Nexo item is not a block: " + itemId);
        }
        NexoBlocks.place(itemId, location);
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }

}
