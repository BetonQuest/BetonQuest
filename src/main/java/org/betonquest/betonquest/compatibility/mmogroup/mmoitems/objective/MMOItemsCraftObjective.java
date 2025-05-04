package org.betonquest.betonquest.compatibility.mmogroup.mmoitems.objective;

import io.lumine.mythic.lib.api.crafting.event.MythicCraftItemEvent;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.crafting.recipe.CraftingRecipe;
import net.Indyuce.mmoitems.api.crafting.recipe.Recipe;
import net.Indyuce.mmoitems.api.event.PlayerUseCraftingStationEvent;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.compatibility.mmogroup.mmoitems.MMOItemsUtils;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import static net.Indyuce.mmoitems.api.event.PlayerUseCraftingStationEvent.StationAction;

/**
 * An objective that listens for the player crafting items using MMOItems.
 */
public class MMOItemsCraftObjective extends CountingObjective implements Listener {
    /**
     * The type of the item to be crafted.
     */
    private final Type itemType;

    /**
     * The ID of the item to be crafted.
     */
    private final String itemId;

    /**
     * Constructs a new MMOItemsCraftObjective.
     *
     * @param instruction  the instruction object representing the objective
     * @param targetAmount the target amount of items to craft
     * @param itemType     the type of the item to be crafted
     * @param itemId       the ID of the item to be crafted
     * @throws QuestException if the syntax is wrong or any error happens while parsing
     */
    public MMOItemsCraftObjective(final Instruction instruction, final Variable<Number> targetAmount, final Type itemType, final String itemId) throws QuestException {
        super(instruction, targetAmount, "items_to_craft");
        this.itemType = itemType;
        this.itemId = itemId;
    }

    /**
     * This is just the basic crafting event for MMOItems vanilla crafting functionality.
     *
     * @param event The event
     */
    @EventHandler
    public void onItemCraft(final CraftItemEvent event) {
        final OnlineProfile onlineProfile = profileProvider.getProfile((Player) event.getWhoClicked());
        final ItemStack craftedItem = event.getRecipe().getResult();
        if (event.getSlotType() == InventoryType.SlotType.RESULT
                && containsPlayer(onlineProfile)
                && isValidItem(craftedItem)
                && checkConditions(onlineProfile)) {
            progressCraftObjective(onlineProfile, craftedItem.getAmount());
        }
    }

    /**
     * This event is called by MMOItems "recipe-amounts" crafting system.
     *
     * @param event The event
     */
    @EventHandler(ignoreCancelled = true)
    public void onRecipeUse(final MythicCraftItemEvent event) {
        final HumanEntity humanEntity = event.getTrigger().getWhoClicked();
        final Player crafter = (Player) humanEntity;
        final OnlineProfile onlineProfile = profileProvider.getProfile(crafter);
        final ItemStack craftedItem = event.getCache().getResultOfOperation().getResultInventory().getFirst();

        if (containsPlayer(onlineProfile)
                && isValidItem(craftedItem)
                && checkConditions(onlineProfile)) {
            progressCraftObjective(onlineProfile, craftedItem.getAmount());
        }
    }

    /**
     * This listener handles items that were crafted in a MMOItems Craftingstation GUI.
     *
     * @param event The event.
     */
    @EventHandler(ignoreCancelled = true)
    public void onRecipeUse(final PlayerUseCraftingStationEvent event) {
        final OnlineProfile onlineProfile = profileProvider.getProfile(event.getPlayer());
        final StationAction action = event.getInteraction();
        final Recipe usedRecipe = event.getRecipe();

        if (containsPlayer(onlineProfile)
                && action != StationAction.INTERACT_WITH_RECIPE
                && action != StationAction.CANCEL_QUEUE
                && usedRecipe instanceof CraftingRecipe
                && checkConditions(onlineProfile)) {

            final ItemStack craftedItem = event.getResult();
            if (isValidItem(craftedItem)) {
                progressCraftObjective(onlineProfile, craftedItem.getAmount());
            }
        }
    }

    private void progressCraftObjective(final OnlineProfile onlineProfile, final int craftedAmount) {
        getCountingData(onlineProfile).progress(craftedAmount);
        completeIfDoneOrNotify(onlineProfile);
    }

    /**
     * This method checks whether the given ItemStack is actually an MMOItem that is looked for in this objective.
     *
     * @return {@code true} if the item matches the requirements; {@code false} otherwise
     */
    private boolean isValidItem(@Nullable final ItemStack itemStack) {
        return MMOItemsUtils.equalsMMOItem(itemStack, itemType, itemId);
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
