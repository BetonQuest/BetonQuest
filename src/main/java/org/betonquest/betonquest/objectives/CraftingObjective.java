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
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 * Player has to craft specified amount of items.
 */
@SuppressWarnings("PMD.CommentRequired")
public class CraftingObjective extends CountingObjective implements Listener {

    private final QuestItem item;

    public CraftingObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction, "items_to_craft");
        item = instruction.getQuestItem();
        targetAmount = instruction.getVarNum();
        preCheckAmountNotLessThanOne(targetAmount);
    }

    private static int calculateCraftAmount(final CraftItemEvent event) {
        final ItemStack result = event.getRecipe().getResult();
        final PlayerInventory inventory = event.getWhoClicked().getInventory();
        final ItemStack[] ingredients = event.getInventory().getMatrix();
        return switch (event.getClick()) {
            case SHIFT_LEFT, SHIFT_RIGHT -> InventoryUtils.calculateShiftCraftAmount(result, inventory, ingredients);
            case CONTROL_DROP -> InventoryUtils.calculateMaximumCraftAmount(result, ingredients);
            case NUMBER_KEY ->
                    InventoryUtils.calculateSwapCraftAmount(result, inventory.getItem(event.getHotbarButton()));
            case SWAP_OFFHAND -> InventoryUtils.calculateSwapCraftAmount(result, inventory.getItemInOffHand());
            case DROP -> InventoryUtils.calculateDropCraftAmount(result, event.getCursor());
            case LEFT, RIGHT -> InventoryUtils.calculateSimpleCraftAmount(result, event.getCursor());
            default -> 0;
        };
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCrafting(final CraftItemEvent event) {
        if (event.getWhoClicked() instanceof final Player player) {
            final OnlineProfile onlineProfile = PlayerConverter.getID(player);
            if (containsPlayer(onlineProfile) && item.compare(event.getRecipe().getResult()) && checkConditions(onlineProfile)) {
                getCountingData(onlineProfile).progress(calculateCraftAmount(event));
                completeIfDoneOrNotify(onlineProfile);
            }
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
