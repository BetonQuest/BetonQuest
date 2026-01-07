package org.betonquest.betonquest.quest.objective.crafting;

import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.bukkit.event.ItemStackCraftedEvent;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.type.ItemWrapper;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.objective.event.ObjectiveFactoryService;
import org.betonquest.betonquest.util.InventoryUtils;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 * Player has to craft specified amount of items.
 */
public class CraftingObjective extends CountingObjective {

    /**
     * The item to be crafted.
     */
    private final Argument<ItemWrapper> item;

    /**
     * Constructor for the CraftingObjective.
     *
     * @param service      the objective factory service
     * @param targetAmount the target amount of items to be crafted
     * @param item         the item to be crafted
     * @throws QuestException if there is an error in the instruction
     */
    public CraftingObjective(final ObjectiveFactoryService service, final Argument<Number> targetAmount,
                             final Argument<ItemWrapper> item) throws QuestException {
        super(service, targetAmount, "items_to_craft");
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
     * @param event         the CraftItemEvent
     * @param onlineProfile the profile of the player that crafted the item
     * @throws QuestException if argument resolving for the profile fails
     */
    public void onCrafting(final CraftItemEvent event, final OnlineProfile onlineProfile) throws QuestException {
        if (containsPlayer(onlineProfile)
                && item.getValue(onlineProfile).matches(event.getInventory().getResult(), onlineProfile)
                && checkConditions(onlineProfile)) {
            getCountingData(onlineProfile).progress(calculateCraftAmount(event, event.getInventory().getResult()));
            completeIfDoneOrNotify(onlineProfile);
        }
    }

    /**
     * Adds the ItemStack from custom craft sources to the progress of this Objective.
     *
     * @param event   the custom source craft event
     * @param profile the profile of the player that crafted the item
     * @throws QuestException if argument resolving for the profile fails
     */
    public void handleCustomCraft(final ItemStackCraftedEvent event, final Profile profile) throws QuestException {
        if (containsPlayer(profile) && checkConditions(profile)) {
            if (item.getValue(profile).getItem(profile).matches(event.getStack())) {
                getCountingData(profile).progress(event.getAmount());
                completeIfDoneOrNotify(profile);
            }
        }
    }
}
