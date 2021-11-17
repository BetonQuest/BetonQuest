package org.betonquest.betonquest.objectives;

import lombok.CustomLog;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.BlockSelector;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.betonquest.betonquest.utils.location.CompoundLocation;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

/**
 * The player must step on the pressure plate
 */
@SuppressWarnings("PMD.CommentRequired")
@CustomLog
public class StepObjective extends Objective implements Listener {
    private static final BlockSelector PRESSURE_PLATE_SELECTOR = getPressurePlateSelector();

    private final CompoundLocation loc;

    public StepObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        template = ObjectiveData.class;
        loc = instruction.getLocation();
    }

    private static BlockSelector getPressurePlateSelector() {
        try {
            return new BlockSelector("*_PRESSURE_PLATE");
        } catch (final InstructionParseException e) {
            LOG.reportException(e);
        }
        return null;
    }

    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.NPathComplexity"})
    @EventHandler(ignoreCancelled = true)
    public void onStep(final PlayerInteractEvent event) {
        // Only fire the event for the main hand to avoid that the event is triggered two times.
        if (event.getHand() == EquipmentSlot.OFF_HAND && event.getHand() != null) {
            return; // off hand packet, ignore.
        }
        if (event.getAction() != Action.PHYSICAL) {
            return;
        }
        final Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) {
            return;
        }
        try {
            final String playerID = PlayerConverter.getID(event.getPlayer());
            final Block block = loc.getLocation(playerID).getBlock();
            if (!clickedBlock.equals(block)) {
                return;
            }

            if (PRESSURE_PLATE_SELECTOR == null || !PRESSURE_PLATE_SELECTOR.match(block.getBlockData().getMaterial())) {
                return;
            }
            if (!containsPlayer(playerID)) {
                return;
            }
            // player stepped on the pressure plate
            if (checkConditions(playerID)) {
                completeObjective(playerID);
            }
        } catch (final QuestRuntimeException e) {
            LOG.warning(instruction.getPackage(), "Error while handling '" + instruction.getID() + "' objective: " + e.getMessage(), e);
        }
    }

    @Override
    public void start() {
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    @Override
    public void stop() {
        HandlerList.unregisterAll(this);
    }

    @Override
    public String getDefaultDataInstruction() {
        return "";
    }

    @Override
    public String getProperty(final String name, final String playerID) {
        if ("location".equalsIgnoreCase(name)) {
            final Block block;
            try {
                block = loc.getLocation(playerID).getBlock();
            } catch (final QuestRuntimeException e) {
                LOG.warning(instruction.getPackage(), "Error while getting location property in '" + instruction.getID() + "' objective: "
                        + e.getMessage(), e);
                return "";
            }
            return "X: " + block.getX() + ", Y: " + block.getY() + ", Z: " + block.getZ();
        }
        return "";
    }
}
