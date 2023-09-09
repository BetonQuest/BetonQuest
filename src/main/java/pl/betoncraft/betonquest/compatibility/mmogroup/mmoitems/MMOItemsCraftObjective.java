package pl.betoncraft.betonquest.compatibility.mmogroup.mmoitems;

import io.lumine.mythic.lib.api.crafting.event.MythicCraftItemEvent;
import io.lumine.mythic.lib.api.item.NBTItem;
import io.lumine.mythic.lib.api.util.ui.FriendlyFeedbackProvider;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.crafting.recipe.CraftingRecipe;
import net.Indyuce.mmoitems.api.crafting.recipe.Recipe;
import net.Indyuce.mmoitems.api.event.PlayerUseCraftingStationEvent;
import net.Indyuce.mmoitems.api.util.message.FFPMMOItems;
import net.Indyuce.mmoitems.manager.TypeManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import static net.Indyuce.mmoitems.api.event.PlayerUseCraftingStationEvent.StationAction;

@SuppressWarnings("PMD.CommentRequired")
public class MMOItemsCraftObjective extends Objective implements Listener {

    private final Type itemType;
    private final String itemId;

    private final int amount;

    public MMOItemsCraftObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        template = CraftData.class;

        final TypeManager typeManager = MMOItems.plugin.getTypes();
        itemType = typeManager.get(instruction.next());
        itemId = instruction.next();

        final String amountStr = instruction.getOptional("amount");
        amount = amountStr == null ? 1 : Integer.parseInt(instruction.getOptional("amount"));
    }

    /**
     * This is just Spigots basic crafting event for
     * MMOItems vanilla crafting functionality.
     */
    @EventHandler
    public void onItemCraft(final CraftItemEvent event) {
        if (event.getSlotType() != InventoryType.SlotType.RESULT) {
            return;
        }

        final String playerID = PlayerConverter.getID((Player) event.getWhoClicked());
        if (!containsPlayer(playerID) || !checkConditions(playerID)) {
            return;
        }
        final ItemStack craftedItem = event.getRecipe().getResult();
        if (isInvalidItem(craftedItem)) {
            return;
        }

        final CraftData playerData = (CraftData) dataMap.get(playerID);
        playerData.craft(craftedItem.getAmount());
        if (playerData.isCompleted()) {
            completeObjective(playerID);
        }
    }

    /**
     * This event is called by MMOItems "recipe-amounts" crafting system.
     */
    @EventHandler(ignoreCancelled = true)
    public void onRecipeUse(final MythicCraftItemEvent event) {
        final HumanEntity humanEntity = event.getTrigger().getWhoClicked();
        final Player crafter = (Player) humanEntity;
        final String playerID = PlayerConverter.getID(crafter);
        if (!containsPlayer(playerID) || !checkConditions(playerID)) {
            return;
        }

        final ItemStack craftedItem = event.getCache().getResultOfOperation().getResultInventory().getFirst();
        if (isInvalidItem(craftedItem)) {
            return;
        }

        final CraftData playerData = (CraftData) dataMap.get(playerID);
        playerData.craft(craftedItem.getAmount());
        if (playerData.isCompleted()) {
            completeObjective(playerID);
        }
    }

    /**
     * This listener handles items that were crafted in a MMOItems Craftingstation GUI.
     */
    @EventHandler(ignoreCancelled = true)
    public void onRecipeUse(final PlayerUseCraftingStationEvent event) {
        final String playerID = PlayerConverter.getID(event.getPlayer());
        if (!containsPlayer(playerID) || !checkConditions(playerID)) {
            return;
        }

        final StationAction action = event.getInteraction();
        if (action == StationAction.INTERACT_WITH_RECIPE || action == StationAction.CANCEL_QUEUE) {
            return;
        }

        final Recipe usedRecipe = event.getRecipe();
        if (!(usedRecipe instanceof CraftingRecipe)) {
            return;
        }

        final CraftingRecipe craftingRecipe = (CraftingRecipe) usedRecipe;

        final ItemStack craftedItem = craftingRecipe.getOutput().getItemStack(new FriendlyFeedbackProvider(FFPMMOItems.get()));
        if (isInvalidItem(craftedItem)) {
            return;
        }

        final CraftData playerData = (CraftData) dataMap.get(playerID);
        playerData.craft(craftedItem.getAmount());
        if (playerData.isCompleted()) {
            completeObjective(playerID);
        }
    }

    /**
     * This method check whether the given ItemStack is actually an MMOItem that is looked for in this objective.
     */
    private boolean isInvalidItem(final ItemStack itemStack) {
        final NBTItem realItemNBT = NBTItem.get(itemStack);
        final String realItemType = realItemNBT.getString("MMOITEMS_ITEM_TYPE");
        final String realItemID = realItemNBT.getString("MMOITEMS_ITEM_ID");

        return !realItemID.equalsIgnoreCase(itemId) || !realItemType.equalsIgnoreCase(itemType.getId());
    }

    @Override
    public void start() {
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    @Override
    public void stop() {
        HandlerList.unregisterAll(this);
    }

    @Override
    public String getDefaultDataInstruction() {
        return String.valueOf(amount);
    }

    @Override
    public String getProperty(final String name, final String playerID) {
        if ("left".equalsIgnoreCase(name)) {
            return Integer.toString(((CraftData) dataMap.get(playerID)).getAmount());
        } else if ("amount".equalsIgnoreCase(name)) {
            return Integer.toString(amount);
        }
        return "";
    }

    public static class CraftData extends ObjectiveData {

        private int itemsLeft;

        public CraftData(final String instruction, final String playerID, final String objID) {
            super(instruction, playerID, objID);
            itemsLeft = Integer.parseInt(instruction);
        }

        private void craft(final int craftedItems) {
            itemsLeft = itemsLeft - craftedItems;
            update();
        }

        private int getAmount() {
            return itemsLeft;
        }

        private boolean isCompleted() {
            return itemsLeft <= 0;
        }

        @Override
        public String toString() {
            return Integer.toString(itemsLeft);
        }
    }
}


