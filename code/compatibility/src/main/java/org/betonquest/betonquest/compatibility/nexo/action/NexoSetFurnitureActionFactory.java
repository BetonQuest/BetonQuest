package org.betonquest.betonquest.compatibility.nexo.action;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.bukkit.Location;
import org.bukkit.Rotation;
import org.bukkit.block.BlockFace;

/**
 * A factory class responsible for parsing and creating instances of {@link NexoSetFurnitureAction}.
 *
 * <p>This factory extracts the Nexo furniture ID, target location, and optional orientation
 * settings (rotation and block face) from the BetonQuest instruction. It applies default
 * values if the orientation parameters are omitted.</p>
 */
public class NexoSetFurnitureActionFactory implements PlayerActionFactory {

    /**
     * The empty default constructor.
     */
    public NexoSetFurnitureActionFactory() {
        // Empty
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<String> itemId = instruction.string().get();
        final Argument<Location> location = instruction.location().get();
        final Argument<Rotation> rotation = instruction.enumeration(Rotation.class).get("rotation", Rotation.NONE);
        final Argument<BlockFace> blockFace = instruction.enumeration(BlockFace.class).get("blockFace", BlockFace.SELF);
        return new NexoSetFurnitureAction(itemId, location, rotation, blockFace);
    }
}
