package org.betonquest.betonquest.objectives;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.conditions.ChestItemCondition;
import org.betonquest.betonquest.events.ChestTakeEvent;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.id.NoID;
import org.betonquest.betonquest.utils.LogUtils;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.betonquest.betonquest.utils.location.CompoundLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryHolder;

import java.util.logging.Level;

/**
 * Requires the player to put items in the chest. Items can optionally NOT
 * disappear once the chest is closed.
 */
@SuppressWarnings("PMD.CommentRequired")
public class ChestPutObjective extends Objective implements Listener {

    private final Condition chestItemCondition;
    private final QuestEvent chestTakeEvent;
    private final CompoundLocation loc;

    public ChestPutObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        template = ObjectiveData.class;
        // extract location
        loc = instruction.getLocation();
        final String location = instruction.current();
        final String items = instruction.next();
        try {
            chestItemCondition = new ChestItemCondition(new Instruction(instruction.getPackage(), new NoID(instruction.getPackage()), "chestitem " + location + " " + items));
        } catch (InstructionParseException | ObjectNotFoundException e) {
            throw new InstructionParseException("Could not create inner chest item condition: " + e.getMessage(), e);
        }
        if (instruction.hasArgument("items-stay")) {
            chestTakeEvent = null;
        } else {
            try {
                chestTakeEvent = new ChestTakeEvent(new Instruction(instruction.getPackage(), new NoID(instruction.getPackage()), "chesttake " + location + " " + items));
            } catch (ObjectNotFoundException e) {
                throw new InstructionParseException("Could not create inner chest take event: " + e.getMessage(), e);
            }
        }

    }

    @SuppressWarnings("PMD.CyclomaticComplexity")
    @EventHandler(ignoreCancelled = true)
    public void onChestClose(final InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }
        final String playerID = PlayerConverter.getID((Player) event.getPlayer());
        if (!containsPlayer(playerID)) {
            return;
        }
        try {
            final Location targetChestLocation = loc.getLocation(playerID);
            final Block block = targetChestLocation.getBlock();
            if (!(block.getState() instanceof InventoryHolder)) {
                final World world = targetChestLocation.getWorld();
                LogUtils.getLogger().log(Level.WARNING,
                        String.format("Error in '%s' chestput objective: Block at location x:%d y:%d z:%d in world '%s' isn't a chest!",
                                instruction.getID().getFullID(),
                                targetChestLocation.getBlockX(),
                                targetChestLocation.getBlockY(),
                                targetChestLocation.getBlockZ(),
                                world == null ? "null" : world.getName()));
                return;
            }
            final InventoryHolder chest = (InventoryHolder) block.getState();
            if (!chest.equals(event.getInventory().getHolder())) {
                return;
            }
            if (chestItemCondition.handle(playerID) && checkConditions(playerID)) {
                completeObjective(playerID);
                if (chestTakeEvent != null) {
                    chestTakeEvent.handle(playerID);
                }
            }
        } catch (QuestRuntimeException e) {
            LogUtils.getLogger().log(Level.WARNING, "Error while handling '" + instruction.getID() + "' objective: " + e.getMessage());
            LogUtils.logThrowable(e);
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
        return "";
    }

}
