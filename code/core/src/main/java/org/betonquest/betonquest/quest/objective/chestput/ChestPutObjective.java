package org.betonquest.betonquest.quest.objective.chestput;

import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableCondition;
import org.betonquest.betonquest.api.quest.objective.event.ObjectiveFactoryService;
import org.betonquest.betonquest.quest.action.IngameNotificationSender;
import org.betonquest.betonquest.quest.action.chest.ChestTakeAction;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Requires the player to put items in the chest. Items can optionally NOT
 * disappear once the chest is closed.
 */
public class ChestPutObjective extends DefaultObjective {

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
     * @param service            the objective factory service
     * @param chestItemCondition the condition to check if the items are in the chest
     * @param chestTakeAction    the action to execute when the items are put in the chest to take them out
     * @param loc                the location of the chest
     * @param occupiedSender     the sender to notify the player if the chest is occupied
     * @param multipleAccess     manages the chest access for one or multiple players
     * @throws QuestException if there is an error in the instruction
     */
    public ChestPutObjective(final ObjectiveFactoryService service,
                             final NullableCondition chestItemCondition, @Nullable final ChestTakeAction chestTakeAction,
                             final Argument<Location> loc, final IngameNotificationSender occupiedSender,
                             final boolean multipleAccess) throws QuestException {
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
     */
    public void onChestOpen(final InventoryOpenEvent event, final OnlineProfile onlineProfile) {
        qeHandler.handle(() -> {
            checkIsInventory(loc.getValue(onlineProfile));
            if (!multipleAccess && !checkForNoOtherPlayer(event)) {
                occupiedSender.sendNotification(onlineProfile);
                event.setCancelled(true);
            }
        });
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
     */
    @SuppressWarnings("PMD.CognitiveComplexity")
    public void onChestClose(final InventoryCloseEvent event, final OnlineProfile onlineProfile) {
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }
        if (!containsPlayer(onlineProfile)) {
            return;
        }
        qeHandler.handle(() -> {
            final Location targetLocation = loc.getValue(onlineProfile);
            checkIsInventory(targetLocation);

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
        });
    }

    private void checkItems(final OnlineProfile onlineProfile) throws QuestException {
        if (chestItemCondition.check(onlineProfile) && checkConditions(onlineProfile)) {
            completeObjective(onlineProfile);
            if (chestTakeAction != null) {
                chestTakeAction.execute(onlineProfile);
            }
        }
    }

    private void checkIsInventory(final Location targetChestLocation) throws QuestException {
        final Block block = targetChestLocation.getBlock();
        if (!(block.getState() instanceof InventoryHolder)) {
            final World world = targetChestLocation.getWorld();
            throw new QuestException(
                    String.format("Block at location x:%d y:%d z:%d in world '%s' isn't a chest!",
                            targetChestLocation.getBlockX(),
                            targetChestLocation.getBlockY(),
                            targetChestLocation.getBlockZ(),
                            world == null ? "null" : world.getName()));
        }
    }

    @Override
    public String getDefaultDataInstruction(final Profile profile) {
        return "";
    }

    @Override
    public String getProperty(final String name, final Profile profile) {
        return "";
    }
}
