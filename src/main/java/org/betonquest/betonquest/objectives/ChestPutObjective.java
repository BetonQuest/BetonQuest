package org.betonquest.betonquest.objectives;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.BetonQuestLogger;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.conditions.ChestItemCondition;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.id.NoID;
import org.betonquest.betonquest.quest.event.chest.ChestTakeEvent;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.betonquest.betonquest.utils.location.CompoundLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.InventoryHolder;

import java.util.List;

/**
 * Requires the player to put items in the chest. Items can optionally NOT
 * disappear once the chest is closed.
 */
@SuppressWarnings("PMD.CommentRequired")
public class ChestPutObjective extends Objective implements Listener {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private static final BetonQuestLogger LOG = BetonQuestLogger.create(ChestPutObjective.class);

    private final Condition chestItemCondition;
    private final Event chestTakeEvent;
    private final CompoundLocation loc;
    /**
     * Argument to manage the chest access for one or multiple players. False by default which means only one player
     * can acess the chest at the same time.
     */
    private final boolean multipleAccess;

    public ChestPutObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        template = ObjectiveData.class;
        // extract location
        loc = instruction.getLocation();
        final String location = instruction.current();
        final String items = instruction.next();
        multipleAccess = Boolean.parseBoolean(instruction.getOptional("multipleaccess"));
        try {
            chestItemCondition = new ChestItemCondition(new Instruction(instruction.getPackage(), new NoID(instruction.getPackage()), "chestitem " + location + " " + items));
        } catch (final InstructionParseException | ObjectNotFoundException e) {
            throw new InstructionParseException("Could not create inner chest item condition: " + e.getMessage(), e);
        }
        if (instruction.hasArgument("items-stay")) {
            chestTakeEvent = null;
        } else {
            chestTakeEvent = new ChestTakeEvent(loc, instruction.getItemList(items));
        }

    }

    /**
     * Permits multiple players to look into the chest, if set.
     *
     * @param event InventoryOpenEvent
     */
    @EventHandler
    public void onChestOpen(final InventoryOpenEvent event) {
        if (!multipleAccess && !checkForNoOtherPlayer(event)) {
            try {
                Config.sendNotify(null, PlayerConverter.getID((Player) event.getPlayer()), "chest_occupied", null);
            } catch (final QuestRuntimeException e) {
                LOG.warn("The notify system was unable to send the message for 'chest_occupied'. Error was: '"
                        + e.getMessage() + "'", e);
            }
            event.setCancelled(true);
        }
    }

    /**
     * Checks if there is no other player that has this inventory open
     *
     * @param event InventoryOpenEvent
     * @return true, if no other player using the inventory, else false
     */
    private boolean checkForNoOtherPlayer(final InventoryOpenEvent event) {
        return event.getInventory().getViewers().equals(List.of(event.getPlayer()));
    }

    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.CognitiveComplexity"})
    @EventHandler(ignoreCancelled = true)
    public void onChestClose(final InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }
        final OnlineProfile onlineProfile = PlayerConverter.getID((Player) event.getPlayer());
        if (!containsPlayer(onlineProfile)) {
            return;
        }
        try {
            final Location targetLocation = loc.getLocation(onlineProfile);
            if (isNotInventory(targetLocation)) {
                return;
            }

            final Location invLocation = event.getInventory().getLocation();
            if (invLocation != null && targetLocation.equals(invLocation.getBlock().getLocation())) {
                checkItems(onlineProfile);
            } else {
                final InventoryHolder holder = event.getInventory().getHolder();
                if (holder instanceof final DoubleChest doubleChest) {
                    final Chest leftChest = (Chest) doubleChest.getLeftSide();
                    final Chest rightChest = (Chest) doubleChest.getRightSide();
                    if (leftChest == null || rightChest == null) {
                        return;
                    }
                    if (leftChest.getLocation().getBlock().getLocation().equals(targetLocation)
                            || rightChest.getLocation().getBlock().getLocation().equals(targetLocation)) {
                        checkItems(onlineProfile);
                    }
                }
            }
        } catch (final QuestRuntimeException e) {
            LOG.warn(instruction.getPackage(), "Error while handling '" + instruction.getID() + "' objective: " + e.getMessage(), e);
        }
    }

    private void checkItems(final OnlineProfile onlineProfile) throws QuestRuntimeException {
        if (chestItemCondition.handle(onlineProfile) && checkConditions(onlineProfile)) {
            completeObjective(onlineProfile);
            if (chestTakeEvent != null) {
                chestTakeEvent.execute(onlineProfile);
            }
        }
    }

    private boolean isNotInventory(final Location targetChestLocation) {
        final Block block = targetChestLocation.getBlock();
        if (!(block.getState() instanceof InventoryHolder)) {
            final World world = targetChestLocation.getWorld();
            LOG.warn(instruction.getPackage(),
                    String.format("Error in '%s' chestput objective: Block at location x:%d y:%d z:%d in world '%s' isn't a chest!",
                            instruction.getID().getFullID(),
                            targetChestLocation.getBlockX(),
                            targetChestLocation.getBlockY(),
                            targetChestLocation.getBlockZ(),
                            world == null ? "null" : world.getName()));
            return true;
        }
        return false;
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
    public String getProperty(final String name, final Profile profile) {
        return "";
    }

}
