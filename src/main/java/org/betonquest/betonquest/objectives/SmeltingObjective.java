package org.betonquest.betonquest.objectives;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.item.QuestItem;
import org.betonquest.betonquest.utils.InventoryUtils;
import org.betonquest.betonquest.utils.PlayerConverter;
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
@SuppressWarnings("PMD.CommentRequired")
public class SmeltingObjective extends CountingObjective implements Listener {

    private final QuestItem item;

    public SmeltingObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction, "items_to_smelt");
        item = instruction.getQuestItem();
        targetAmount = instruction.getVarNum();
        preCheckAmountNotLessThanOne(targetAmount);
    }

    @EventHandler(ignoreCancelled = true)
    public void onSmelting(final InventoryClickEvent event) {
        final InventoryType inventoryType = event.getInventory().getType();
        if (isSmeltingResultExtraction(event, inventoryType)) {
            final OnlineProfile onlineProfile = PlayerConverter.getID((Player) event.getWhoClicked());
            if (containsPlayer(onlineProfile) && item.compare(event.getCurrentItem()) && checkConditions(onlineProfile)) {
                final int taken = calculateTakeAmount(event);
                getCountingData(onlineProfile).progress(taken);
                completeIfDoneOrNotify(onlineProfile);
            }
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

    @SuppressWarnings("PMD.CyclomaticComplexity")
    private int calculateTakeAmount(final InventoryClickEvent event) {
        final ItemStack result = event.getCurrentItem();
        final PlayerInventory inventory = event.getWhoClicked().getInventory();
        return switch (event.getClick()) {
            case SHIFT_LEFT, SHIFT_RIGHT ->
                    Math.min(InventoryUtils.calculateSpaceForItem(inventory, result), result.getAmount());
            case CONTROL_DROP -> InventoryUtils.calculateSpaceForItem(inventory, result);
            case NUMBER_KEY ->
                    InventoryUtils.calculateSwapCraftAmount(result, inventory.getItem(event.getHotbarButton()));
            case SWAP_OFFHAND -> InventoryUtils.calculateSwapCraftAmount(result, inventory.getItemInOffHand());
            case DROP -> 1;
            case RIGHT -> {
                if (InventoryUtils.isEmptySlot(event.getCursor())) {
                    yield (result.getAmount() + 1) / 2;
                }
                yield InventoryUtils.calculateSimpleCraftAmount(result, event.getCursor());
            }
            case LEFT -> InventoryUtils.calculateSimpleCraftAmount(result, event.getCursor());
            default -> 0;
        };
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
