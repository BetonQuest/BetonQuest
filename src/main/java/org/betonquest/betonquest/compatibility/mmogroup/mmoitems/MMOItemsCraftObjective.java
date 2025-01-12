package org.betonquest.betonquest.compatibility.mmogroup.mmoitems;

import io.lumine.mythic.lib.api.crafting.event.MythicCraftItemEvent;
import io.lumine.mythic.lib.api.util.ui.FriendlyFeedbackProvider;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.crafting.recipe.CraftingRecipe;
import net.Indyuce.mmoitems.api.crafting.recipe.Recipe;
import net.Indyuce.mmoitems.api.event.PlayerUseCraftingStationEvent;
import net.Indyuce.mmoitems.api.util.message.FFPMMOItems;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.utils.PlayerConverter;
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

@SuppressWarnings("PMD.CommentRequired")
public class MMOItemsCraftObjective extends CountingObjective implements Listener {
    private final Type itemType;

    private final String itemId;

    public MMOItemsCraftObjective(final Instruction instruction) throws QuestException {
        super(instruction, "items_to_craft");

        itemType = MMOItemsUtils.getMMOItemType(instruction.next());
        itemId = instruction.next();

        targetAmount = instruction.getVarNum(instruction.getOptional("amount", "1"), VariableNumber.NOT_LESS_THAN_ONE_CHECKER);
    }

    /**
     * This is just Spigots basic crafting event for
     * MMOItems vanilla crafting functionality.
     *
     * @param event The event
     */
    @EventHandler
    public void onItemCraft(final CraftItemEvent event) {
        final OnlineProfile onlineProfile = PlayerConverter.getID((Player) event.getWhoClicked());
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
        final OnlineProfile onlineProfile = PlayerConverter.getID(crafter);
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
        final OnlineProfile onlineProfile = PlayerConverter.getID(event.getPlayer());
        final StationAction action = event.getInteraction();
        final Recipe usedRecipe = event.getRecipe();

        if (containsPlayer(onlineProfile)
                && action != StationAction.INTERACT_WITH_RECIPE
                && action != StationAction.CANCEL_QUEUE
                && usedRecipe instanceof final CraftingRecipe craftingRecipe
                && checkConditions(onlineProfile)) {

            final ItemStack craftedItem = craftingRecipe.getOutput().getItemStack(new FriendlyFeedbackProvider(FFPMMOItems.get()));
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
