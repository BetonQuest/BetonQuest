package org.betonquest.betonquest.compatibility.nexo.action;

import com.nexomc.nexo.api.NexoFurniture;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.bukkit.Location;
import org.bukkit.Rotation;
import org.bukkit.block.BlockFace;

/**
 * An action that places a Nexo custom furniture at a specific location.
 *
 * <p>This action resolves the furniture ID, target location, rotation, and block face
 * orientation from the profile. It validates the item ID before placing the furniture
 * using the Nexo API.</p>
 */
public class NexoSetFurnitureAction implements PlayerAction {

    /**
     * The Nexo furniture item ID.
     */
    private final Argument<String> itemIdArgument;

    /**
     * The target location for the furniture placement.
     */
    private final Argument<Location> locationArgument;

    /**
     * The rotation for the placed furniture.
     */
    private final Argument<Rotation> rotationArgument;

    /**
     * The block face orientation for the furniture.
     */
    private final Argument<BlockFace> blockFaceArgument;

    /**
     * Creates a new NexoSetFurnitureAction.
     *
     * @param itemIdArgument    the argument for the furniture item ID
     * @param locationArgument  the argument for the target location
     * @param rotationArgument  the argument for the rotation
     * @param blockFaceArgument the argument for the block face orientation
     */
    public NexoSetFurnitureAction(
            final Argument<String> itemIdArgument,
            final Argument<Location> locationArgument,
            final Argument<Rotation> rotationArgument,
            final Argument<BlockFace> blockFaceArgument
    ) {
        this.itemIdArgument = itemIdArgument;
        this.locationArgument = locationArgument;
        this.rotationArgument = rotationArgument;
        this.blockFaceArgument = blockFaceArgument;
    }

    @Override
    public void execute(final Profile profile) throws QuestException {
        final Location location = locationArgument.getValue(profile);
        final String itemId = itemIdArgument.getValue(profile);

        if (!NexoFurniture.isFurniture(itemId)) {
            throw new QuestException("Nexo item is not a furniture: " + itemId);
        }
        NexoFurniture.place(itemId, location, rotationArgument.getValue(profile), blockFaceArgument.getValue(profile));
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }

}
