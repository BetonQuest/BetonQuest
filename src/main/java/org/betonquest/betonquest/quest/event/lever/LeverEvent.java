package org.betonquest.betonquest.quest.event.lever;

import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.location.CompoundLocation;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Powerable;

/**
 * This event turns on, of or toggles levers.
 */
public class LeverEvent implements Event {

    /**
     * The type of state to set the lever to.
     */
    private final StateType stateType;

    /**
     * The location of the lever.
     */
    private final CompoundLocation location;

    /**
     * Create a new lever event.
     *
     * @param stateType the type of state to set the lever to
     * @param location  the location of the lever
     */
    public LeverEvent(final StateType stateType, final CompoundLocation location) {
        this.stateType = stateType;
        this.location = location;
    }

    @Override
    public void execute(final Profile profile) throws QuestRuntimeException {
        final Block block = location.getLocation(profile).getBlock();

        if (!block.getType().equals(Material.LEVER)) {
            throw new QuestRuntimeException("There is no lever at x: " + block.getX() + " y: " + block.getY() + " z: "
                    + block.getZ() + " in world '" + block.getWorld().getName() + "'");
        }

        final Powerable lever = (Powerable) block.getBlockData();
        lever.setPowered(stateType.apply(lever.isPowered()));
        block.setBlockData(lever);
    }
}
