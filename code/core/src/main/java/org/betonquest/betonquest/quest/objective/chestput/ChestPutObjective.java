package org.betonquest.betonquest.quest.objective.chestput;

import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.condition.NullableCondition;
import org.betonquest.betonquest.api.quest.objective.service.ObjectiveService;
import org.betonquest.betonquest.quest.action.IngameNotificationSender;
import org.betonquest.betonquest.quest.action.chest.ChestTakeAction;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * Requires the player to put items in the chest. Items can optionally NOT
 * disappear once the chest is closed.
 */
public class ChestPutObjective extends DefaultObjective {

    /**
     * Inventory types without a persistent inventory.
     */
    private static final Set<InventoryType> IGNORED_TYPES = EnumSet.of(InventoryType.WORKBENCH, InventoryType.CRAFTING,
            InventoryType.ENCHANTING, InventoryType.PLAYER, InventoryType.CREATIVE, InventoryType.MERCHANT, InventoryType.ANVIL,
            InventoryType.SMITHING, InventoryType.BEACON, InventoryType.LOOM, InventoryType.CARTOGRAPHY, InventoryType.GRINDSTONE,
            InventoryType.STONECUTTER, InventoryType.COMPOSTER);

    /**
     * Condition to check if the items are in the chest.
     */
    private final NullableCondition chestItemCondition;

    /**
     * Action to execute when the items are put in the chest to take them out.
     */
    @Nullable
    private final ChestTakeAction chestTakeAction;

    /**
     * Location of the chest.
     */
    private final Argument<Location> loc;

    /**
     * Sender to notify the player if the chest is occupied.
     */
    private final IngameNotificationSender occupiedSender;

    /**
     * Manages the chest access for one or multiple players.
     */
    private final boolean multipleAccess;

    /**
     * Constructor for the ChestPutObjective.
     *
     * @param service            the objective service
     * @param chestItemCondition the condition to check if the items are in the chest
     * @param chestTakeAction    the action to execute when the items are put in the chest to take them out
     * @param loc                the location of the chest
     * @param occupiedSender     the sender to notify the player if the chest is occupied
     * @param multipleAccess     manages the chest access for one or multiple players
     */
    public ChestPutObjective(final ObjectiveService service,
                             final NullableCondition chestItemCondition, @Nullable final ChestTakeAction chestTakeAction,
                             final Argument<Location> loc, final IngameNotificationSender occupiedSender,
                             final boolean multipleAccess) {
        super(service);
        this.chestItemCondition = chestItemCondition;
        this.chestTakeAction = chestTakeAction;
        this.loc = loc;
        this.occupiedSender = occupiedSender;
        this.multipleAccess = multipleAccess;
    }

    /**
     * Permits multiple players to look into the chest, if set.
     *
     * @param event         InventoryOpenEvent
     * @param onlineProfile the profile of the player that opened the chest
     * @throws QuestException if argument resolving for the profile fails
     */
    public void onChestOpen(final InventoryOpenEvent event, final OnlineProfile onlineProfile) throws QuestException {
        if (multipleAccess || IGNORED_TYPES.contains(event.getInventory().getType())) {
            return;
        }
        if (!checkForNoOtherPlayer(event) && isRelevantBlock(event, onlineProfile)) {
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
     * @param event         the event that triggered this method
     * @param onlineProfile the profile of the player that closed the chest
     * @throws QuestException if argument resolving for the profile fails
     */
    public void onChestClose(final InventoryCloseEvent event, final OnlineProfile onlineProfile) throws QuestException {
        if (IGNORED_TYPES.contains(event.getInventory().getType())) {
            return;
        }
        if (isRelevantBlock(event, onlineProfile)) {
            checkItems(onlineProfile);
        }
    }

    private boolean isRelevantBlock(final InventoryEvent event, final OnlineProfile onlineProfile) throws QuestException {
        final Location targetLocation = loc.getValue(onlineProfile);

        final Location invLocation = event.getInventory().getLocation();
        if (invLocation != null && targetLocation.equals(invLocation.getBlock().getLocation())) {
            checkIsInventory(targetLocation);
            return true;
        }

        final InventoryHolder holder = event.getInventory().getHolder(false);
        if (holder instanceof final DoubleChest doubleChest) {
            final Chest leftChest = (Chest) doubleChest.getLeftSide();
            final Chest rightChest = (Chest) doubleChest.getRightSide();
            if (leftChest == null || rightChest == null) {
                return false;
            }
            return leftChest.getLocation().getBlock().getLocation().equals(targetLocation)
                    || rightChest.getLocation().getBlock().getLocation().equals(targetLocation);
        }
        return false;
    }

    private void checkItems(final OnlineProfile onlineProfile) throws QuestException {
        if (chestItemCondition.check(onlineProfile)) {
            getService().complete(onlineProfile);
            if (chestTakeAction != null) {
                chestTakeAction.execute(onlineProfile);
            }
        }
    }

    private void checkIsInventory(final Location targetChestLocation) throws QuestException {
        final Block block = targetChestLocation.getBlock();
        if (!(block.getState(false) instanceof InventoryHolder)) {
            final World world = targetChestLocation.getWorld();
            throw new QuestException(
                    "Block at location x:%d y:%d z:%d in world '%s' isn't a chest!".formatted(
                            targetChestLocation.getBlockX(),
                            targetChestLocation.getBlockY(),
                            targetChestLocation.getBlockZ(),
                            world == null ? "null" : world.getName()));
        }
    }
}
