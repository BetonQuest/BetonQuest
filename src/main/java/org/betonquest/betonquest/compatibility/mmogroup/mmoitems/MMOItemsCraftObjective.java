package org.betonquest.betonquest.compatibility.mmogroup.mmoitems;

import io.lumine.mythic.lib.api.item.NBTItem;
import io.lumine.mythic.lib.api.util.ui.FriendlyFeedbackProvider;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.crafting.recipe.CraftingRecipe;
import net.Indyuce.mmoitems.api.crafting.recipe.Recipe;
import net.Indyuce.mmoitems.api.event.CraftMMOItemEvent;
import net.Indyuce.mmoitems.api.event.PlayerUseCraftingStationEvent;
import net.Indyuce.mmoitems.api.util.message.FFPMMOItems;
import net.Indyuce.mmoitems.manager.TypeManager;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import static net.Indyuce.mmoitems.api.event.PlayerUseCraftingStationEvent.StationAction;

@SuppressWarnings("PMD.CommentRequired")
public class MMOItemsCraftObjective extends CountingObjective implements Listener {

    private final Type itemType;
    private final String itemId;

    public MMOItemsCraftObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction, "items_to_craft");

        final TypeManager typeManager = MMOItems.plugin.getTypes();
        itemType = typeManager.get(instruction.next());
        itemId = instruction.next();

        final String amountStr = instruction.getOptional("amount");
        targetAmount = amountStr == null ? 1 : Integer.parseInt(instruction.getOptional("amount"));
    }

    /**
     * This is just Spigots basic crafting event for
     * MMOItems vanilla crafting functionality.
     *
     * @param event The event
     */
    @EventHandler
    public void onItemCraft(final CraftItemEvent event) {
        final OnlineProfile profile = PlayerConverter.getID((Player) event.getWhoClicked());
        final ItemStack craftedItem = event.getRecipe().getResult();
        if (event.getSlotType() == InventoryType.SlotType.RESULT
                && containsPlayer(profile)
                && isValidItem(craftedItem)
                && checkConditions(profile)) {
            progressCraftObjective(profile, craftedItem.getAmount());
        }
    }

    /**
     * This event is called by MMOItems "recipe-amounts" crafting system.
     *
     * @param event The event
     */
    @EventHandler(ignoreCancelled = true)
    public void onRecipeUse(final CraftMMOItemEvent event) {
        final OnlineProfile profile = PlayerConverter.getID(event.getPlayer());
        final ItemStack craftedItem = event.getResult();

        if (containsPlayer(profile)
                && isValidItem(craftedItem)
                && checkConditions(profile)) {
            progressCraftObjective(profile, craftedItem.getAmount());
        }
    }

    /**
     * This listener handles items that were crafted in a MMOItems Craftingstation GUI.
     *
     * @param event The event.
     */
    @EventHandler(ignoreCancelled = true)
    public void onRecipeUse(final PlayerUseCraftingStationEvent event) {
        final OnlineProfile profile = PlayerConverter.getID(event.getPlayer());
        final StationAction action = event.getInteraction();
        final Recipe usedRecipe = event.getRecipe();

        if (containsPlayer(profile)
                && action != StationAction.INTERACT_WITH_RECIPE
                && action != StationAction.CANCEL_QUEUE
                && usedRecipe instanceof CraftingRecipe
                && checkConditions(profile)) {
            final CraftingRecipe craftingRecipe = (CraftingRecipe) usedRecipe;

            final ItemStack craftedItem = craftingRecipe.getOutput().getItemStack(new FriendlyFeedbackProvider(FFPMMOItems.get()));
            if (isValidItem(craftedItem)) {
                progressCraftObjective(profile, craftedItem.getAmount());
            }
        }
    }

    private void progressCraftObjective(final Profile profile, final int craftedAmount) {
        getCountingData(profile).progress(craftedAmount);
        completeIfDoneOrNotify(profile);
    }

    /**
     * This method checks whether the given ItemStack is actually an MMOItem that is looked for in this objective.
     *
     * @return {@code true} if the item matches the requirements; {@code false} otherwise
     */
    private boolean isValidItem(final ItemStack itemStack) {
        if (itemStack == null) {
            return false;
        }
        final NBTItem realItemNBT = NBTItem.get(itemStack);
        final String realItemType = realItemNBT.getString("MMOITEMS_ITEM_TYPE");
        final String realItemID = realItemNBT.getString("MMOITEMS_ITEM_ID");

        return realItemID.equalsIgnoreCase(itemId) && realItemType.equalsIgnoreCase(itemType.getId());
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
