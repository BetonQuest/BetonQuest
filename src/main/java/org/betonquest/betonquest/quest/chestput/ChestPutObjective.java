package org.betonquest.betonquest.quest.chestput;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableCondition;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.betonquest.betonquest.quest.event.IngameNotificationSender;
import org.betonquest.betonquest.quest.event.chest.ChestTakeEvent;
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
public class ChestPutObjective extends Objective implements Listener {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * Condition to check if the items are in the chest.
     */
    private final NullableCondition chestItemCondition;

    /**
     * Event to execute when the items are put in the chest to take them out.
     */
    @Nullable
    private final ChestTakeEvent chestTakeEvent;

    /**
     * Location of the chest.
     */
    private final VariableLocation loc;

    /**
     * Sender to notify the player if the chest is occupied.
     */
    private final IngameNotificationSender occupiedSender;

    /**
     * Argument to manage the chest access for one or multiple players. False by default which means only one player
     * can access the chest at the same time.
     */
    private final boolean multipleAccess;

    /**
     * Constructor for the ChestPutObjective.
     *
     * @param instruction        the instruction that created this objective
     * @param log                the logger for this objective
     * @param chestItemCondition the condition to check if the items are in the chest
     * @param chestTakeEvent     the event to execute when the items are put in the chest to take them out
     * @param loc                the location of the chest
     * @param occupiedSender     the sender to notify the player if the chest is occupied
     * @param multipleAccess     argument to manage the chest access for one or multiple players
     * @throws QuestException if there is an error in the instruction
     */
    public ChestPutObjective(final Instruction instruction, final BetonQuestLogger log,
                             final NullableCondition chestItemCondition, @Nullable final ChestTakeEvent chestTakeEvent,
                             final VariableLocation loc, final IngameNotificationSender occupiedSender,
                             final boolean multipleAccess) throws QuestException {
        super(instruction);
        this.log = log;
        this.chestItemCondition = chestItemCondition;
        this.chestTakeEvent = chestTakeEvent;
        this.loc = loc;
        this.occupiedSender = occupiedSender;
        this.multipleAccess = multipleAccess;
    }

    /**
     * Permits multiple players to look into the chest, if set.
     *
     * @param event InventoryOpenEvent
     */
    @EventHandler
    public void onChestOpen(final InventoryOpenEvent event) {
        final OnlineProfile onlineProfile = profileProvider.getProfile((Player) event.getPlayer());
        try {
            if (!checkIsInventory(loc.getValue(onlineProfile))) {
                return;
            }
        } catch (final QuestException e) {
            log.warn(instruction.getPackage(), "Error while handling '" + instruction.getID() + "' objective: " + e.getMessage(), e);
        }
        if (!multipleAccess && !checkForNoOtherPlayer(event)) {
            occupiedSender.sendNotification(onlineProfile);
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

    /**
     * Tracks when a chest is closed and checks if the items are in the chest.
     *
     * @param event the event that triggered this method
     */
    @SuppressWarnings({"PMD.CognitiveComplexity", "PMD.CyclomaticComplexity"})
    @EventHandler(ignoreCancelled = true)
    public void onChestClose(final InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }
        final OnlineProfile onlineProfile = profileProvider.getProfile((Player) event.getPlayer());
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
