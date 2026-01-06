package org.betonquest.betonquest.quest.action.lever;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.action.nullable.NullableAction;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.block.data.type.Switch;
import org.jetbrains.annotations.Nullable;

/**
 * This action turns on, of or toggles levers.
 */
public class LeverAction implements NullableAction {

    /**
     * The type of state to set the lever to.
     */
    private final Argument<StateType> stateType;

    /**
     * The location of the lever.
     */
    private final Argument<Location> location;

    /**
     * Create a new lever action.
     *
     * @param stateType the type of state to set the lever to
     * @param location  the location of the lever
     */
    public LeverAction(final Argument<StateType> stateType, final Argument<Location> location) {
        this.stateType = stateType;
        this.location = location;
    }

    @Override
    public void execute(@Nullable final Profile profile) throws QuestException {
        final Block block = location.getValue(profile).getBlock();

        if (block.getType() != Material.LEVER) {
            throw new QuestException("There is no lever at x: " + block.getX() + " y: " + block.getY() + " z: "
                    + block.getZ() + " in world '" + block.getWorld().getName() + "'");
        }

        final Switch lever = (Switch) block.getBlockData();
        lever.setPowered(stateType.getValue(profile).apply(lever.isPowered()));
        block.setBlockData(lever);
        updateBlocksAround(block, lever);
    }

    private void updateBlocksAround(final Block block, final Switch powerableSwitch) {
        final BlockFace attachedTo = switch (powerableSwitch.getAttachedFace()) {
            case FLOOR -> BlockFace.DOWN;
            case CEILING -> BlockFace.UP;
            default -> powerableSwitch.getFacing().getOppositeFace();
        };
        final Block relative = block.getRelative(attachedTo);

        final BlockState relativeState = relative.getState();
        if (relativeState instanceof final Container container) {
            container.getInventory().clear();
        }
        relative.setType(Material.AIR, false);
        relativeState.update(true);
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
