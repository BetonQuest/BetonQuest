package org.betonquest.betonquest.quest.objective.step;

import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.type.BlockSelector;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.objective.event.ObjectiveFactoryService;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

/**
 * The player must step on the pressure plate.
 */
public class StepObjective extends DefaultObjective {

    /**
     * The key for the location property.
     */
    private static final String LOCATION_KEY = "location";

    /**
     * The location of the pressure plate.
     */
    private final Argument<Location> loc;

    /**
     * The selector for the pressure plate block.
     */
    private final BlockSelector pressurePlateSelector;

    /**
     * Constructor for the StepObjective.
     *
     * @param service               the objective factory service
     * @param loc                   the location of the pressure plate
     * @param pressurePlateSelector the selector for the pressure plate block
     * @throws QuestException if there is an error in the instruction
     */
    public StepObjective(final ObjectiveFactoryService service, final Argument<Location> loc, final BlockSelector pressurePlateSelector) throws QuestException {
        super(service);
        this.loc = loc;
        this.pressurePlateSelector = pressurePlateSelector;
    }

    /**
     * Check if the player stepped on the pressure plate.
     *
     * @param event         the PlayerInteractEvent
     * @param onlineProfile the profile of the player that interacted with the pressure plate
     * @throws QuestException if argument resolving for the profile fails
     */
    public void onStep(final PlayerInteractEvent event, final OnlineProfile onlineProfile) throws QuestException {
        if (event.getHand() == EquipmentSlot.OFF_HAND && event.getHand() != null) {
            return;
        }
        if (event.getAction() != Action.PHYSICAL) {
            return;
        }
        final Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) {
            return;
        }
        final Block block = loc.getValue(onlineProfile).getBlock();
        if (!clickedBlock.equals(block)) {
            return;
        }

        if (!pressurePlateSelector.match(block.getBlockData().getMaterial())) {
            return;
        }
        completeObjective(onlineProfile);
    }

    @Override
    public String getProperty(final String name, final Profile profile) throws QuestException {
        if (LOCATION_KEY.equalsIgnoreCase(name)) {
            final Block block = loc.getValue(profile).getBlock();
            return "X: " + block.getX() + ", Y: " + block.getY() + ", Z: " + block.getZ();
        }
        return "";
    }
}
