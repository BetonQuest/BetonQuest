package org.betonquest.betonquest.quest.objective.smelt;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.Item;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.betonquest.betonquest.util.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 * Requires the player to smelt some amount of items.
 */
public class SmeltingObjective extends CountingObjective implements Listener {

    /**
     * The item to be smelted.
     */
    private final Variable<Item> item;

    /**
     * Constructor for the SmeltingObjective.
     *
     * @param instruction  the instruction that created this objective
     * @param targetAmount the target amount of items to be smelted
     * @param item         the item to be smelted
     * @throws QuestException if there is an error in the instruction
     */
    public SmeltingObjective(final Instruction instruction, final Variable<Number> targetAmount, final Variable<Item> item)
            throws QuestException {
        super(instruction, targetAmount, "items_to_smelt");
        this.item = item;
    }

    /**
     * Check if the item matches the one in the furnace.
     *
     * @param event the event to check
     */
    @EventHandler(ignoreCancelled = true)
    public void onSmelting(final InventoryClickEvent event) {
        final InventoryType inventoryType = event.getInventory().getType();
        if (isSmeltingResultExtraction(event, inventoryType)) {
            final OnlineProfile onlineProfile = profileProvider.getProfile((Player) event.getWhoClicked());
            qeHandler.handle(() -> {
                if (containsPlayer(onlineProfile)
                        && item.getValue(onlineProfile).matches(event.getCurrentItem(), onlineProfile)
                        && checkConditions(onlineProfile)) {
                    final int taken = calculateTakeAmount(event);
                    getCountingData(onlineProfile).progress(taken);
                    completeIfDoneOrNotify(onlineProfile);
                }
            });
        }
    }

    private boolean isSmeltingResultExtraction(final InventoryClickEvent event, final InventoryType inventoryType) {
        return (inventoryType == InventoryType.FURNACE
                || inventoryType == InventoryType.SMOKER
                || inventoryType == InventoryType.BLAST_FURNACE)
                && event.getWhoClicked() instanceof Player
                && event.getRawSlot() == 2
                && !InventoryUtils.isEmptySlot(event.getCurrentItem());
    }

    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.ImplicitSwitchFallThrough"})
    private int calculateTakeAmount(final InventoryClickEvent event) {
        final ItemStack result = event.getCurrentItem();
        final PlayerInventory inventory = event.getWhoClicked().getInventory();
        switch (event.getClick()) {
            case SHIFT_LEFT:
            case SHIFT_RIGHT:
                return Math.min(InventoryUtils.calculateSpaceForItem(inventory, result), result.getAmount());
            case CONTROL_DROP:
                return InventoryUtils.calculateSpaceForItem(inventory, result);
            case NUMBER_KEY:
                return InventoryUtils.calculateSwapCraftAmount(result, inventory.getItem(event.getHotbarButton()));
            case SWAP_OFFHAND:
                return InventoryUtils.calculateSwapCraftAmount(result, inventory.getItemInOffHand());
            case DROP:
                return 1;
            case RIGHT:
                if (InventoryUtils.isEmptySlot(event.getCursor())) {
                    return (result.getAmount() + 1) / 2;
                }
            case LEFT:
                return InventoryUtils.calculateSimpleCraftAmount(result, event.getCursor());
            default:
                return 0;
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
}
