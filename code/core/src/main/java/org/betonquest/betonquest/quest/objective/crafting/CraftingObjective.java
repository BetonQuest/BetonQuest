package org.betonquest.betonquest.quest.objective.crafting;

import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.bukkit.event.ItemStackCraftedEvent;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.QuestItemWrapper;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.util.InventoryUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 * Player has to craft specified amount of items.
 */
public class CraftingObjective extends CountingObjective implements Listener {

    /**
     * The item to be crafted.
     */
    private final Variable<QuestItemWrapper> item;

    /**
     * Constructor for the CraftingObjective.
     *
     * @param instruction  the instruction that created this objective
     * @param targetAmount the target amount of items to be crafted
     * @param item         the item to be crafted
     * @throws QuestException if there is an error in the instruction
     */
    public CraftingObjective(final Instruction instruction, final Variable<Number> targetAmount,
                             final Variable<QuestItemWrapper> item) throws QuestException {
        super(instruction, targetAmount, "items_to_craft");
        this.item = item;
    }

    private int calculateCraftAmount(final CraftItemEvent event, final ItemStack result) {
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

    /**
     * Checks if the player has crafted the item.
     *
     * @param event the CraftItemEvent
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCrafting(final CraftItemEvent event) {
        if (event.getWhoClicked() instanceof final Player player) {
            final OnlineProfile onlineProfile = profileProvider.getProfile(player);
            qeHandler.handle(() -> {
                if (containsPlayer(onlineProfile)
                        && item.getValue(onlineProfile).matches(event.getInventory().getResult(), onlineProfile)
                        && checkConditions(onlineProfile)) {
                    getCountingData(onlineProfile).progress(calculateCraftAmount(event, event.getInventory().getResult()));
                    completeIfDoneOrNotify(onlineProfile);
                }
            });
        }
    }

    /**
     * Adds the ItemStack from custom craft sources to the progress of this Objective.
     *
     * @param event the custom source craft event
     */
    @EventHandler
    public void handleCustomCraft(final ItemStackCraftedEvent event) {
        final Profile profile = event.getProfile();
        if (containsPlayer(profile) && checkConditions(profile)) {
            qeHandler.handle(() -> {
                if (item.getValue(profile).getItem(profile).matches(event.getStack())) {
                    getCountingData(profile).progress(event.getAmount());
                    completeIfDoneOrNotify(profile);
                }
            });
        }
    }
}
