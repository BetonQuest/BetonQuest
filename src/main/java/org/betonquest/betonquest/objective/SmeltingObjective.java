package org.betonquest.betonquest.objective;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.VariableArgument;
import org.betonquest.betonquest.item.QuestItem;
import org.betonquest.betonquest.util.InventoryUtils;
import org.betonquest.betonquest.util.PlayerConverter;
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

    public SmeltingObjective(final Instruction instruction) throws QuestException {
        super(instruction, "items_to_smelt");
        item = instruction.getQuestItem();
        targetAmount = instruction.get(VariableArgument.NUMBER_NOT_LESS_THAN_ONE);
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
