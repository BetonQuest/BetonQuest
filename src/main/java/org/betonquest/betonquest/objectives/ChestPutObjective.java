package org.betonquest.betonquest.objectives;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableCondition;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.Item;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.betonquest.betonquest.quest.condition.chest.ChestItemCondition;
import org.betonquest.betonquest.quest.event.chest.ChestTakeEvent;
import org.betonquest.betonquest.utils.PlayerConverter;
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
import org.jetbrains.annotations.Nullable;

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
    private final BetonQuestLogger log;

    private final NullableCondition chestItemCondition;

    @Nullable
    private final ChestTakeEvent chestTakeEvent;

    private final VariableLocation loc;

    /**
     * Argument to manage the chest access for one or multiple players. False by default which means only one player
     * can acess the chest at the same time.
     */
    private final boolean multipleAccess;

    public ChestPutObjective(final Instruction instruction) throws QuestException {
        super(instruction);
        final BetonQuestLoggerFactory loggerFactory = BetonQuest.getInstance().getLoggerFactory();
        this.log = loggerFactory.create(getClass());
        // extract location
        loc = instruction.get(VariableLocation::new);
        final Item[] items = instruction.getItemList();
        multipleAccess = Boolean.parseBoolean(instruction.getOptional("multipleaccess"));
        chestItemCondition = new ChestItemCondition(loc, items);
        if (instruction.hasArgument("items-stay")) {
            chestTakeEvent = null;
        } else {
            chestTakeEvent = new ChestTakeEvent(loc, items);
        }
    }

    /**
     * Permits multiple players to look into the chest, if set.
     *
     * @param event InventoryOpenEvent
     */
    @EventHandler
    public void onChestOpen(final InventoryOpenEvent event) {
        final OnlineProfile onlineProfile = PlayerConverter.getID((Player) event.getPlayer());
        try {
            if (!checkIsInventory(loc.getValue(onlineProfile))) {
                return;
            }
        } catch (final QuestException e) {
            log.warn(instruction.getPackage(), "Error while handling '" + instruction.getID() + "' objective: " + e.getMessage(), e);
        }
        if (!multipleAccess && !checkForNoOtherPlayer(event)) {
            try {
                Config.sendNotify(null, onlineProfile, "chest_occupied", null);
            } catch (final QuestException e) {
                log.warn("The notify system was unable to send the message for 'chest_occupied'. Error was: '"
                        + e.getMessage() + "'", e);
            }
            event.setCancelled(true);
        }
    }

    /**
     * Checks if there is no other player that has this inventory open.
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
            final Location targetLocation = loc.getValue(onlineProfile);
            if (!checkIsInventory(targetLocation)) {
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
        } catch (final QuestException e) {
            log.warn(instruction.getPackage(), "Error while handling '" + instruction.getID() + "' objective: " + e.getMessage(), e);
        }
    }

    private void checkItems(final OnlineProfile onlineProfile) throws QuestException {
        if (chestItemCondition.check(onlineProfile) && checkConditions(onlineProfile)) {
            completeObjective(onlineProfile);
            if (chestTakeEvent != null) {
                chestTakeEvent.execute(onlineProfile);
            }
        }
    }

    private boolean checkIsInventory(final Location targetChestLocation) {
        final Block block = targetChestLocation.getBlock();
        if (!(block.getState() instanceof InventoryHolder)) {
            final World world = targetChestLocation.getWorld();
            log.warn(instruction.getPackage(),
                    String.format("Error in '%s' chestput objective: Block at location x:%d y:%d z:%d in world '%s' isn't a chest!",
                            instruction.getID().getFullID(),
                            targetChestLocation.getBlockX(),
                            targetChestLocation.getBlockY(),
                            targetChestLocation.getBlockZ(),
                            world == null ? "null" : world.getName()));
            return false;
        }
        return true;
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
