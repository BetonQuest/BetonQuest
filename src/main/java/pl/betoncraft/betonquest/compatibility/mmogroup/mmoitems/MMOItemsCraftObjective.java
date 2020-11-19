package pl.betoncraft.betonquest.compatibility.mmogroup.mmoitems;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.event.CraftMMOItemEvent;
import net.Indyuce.mmoitems.api.event.PlayerUseCraftingStationEvent;
import net.Indyuce.mmoitems.manager.TypeManager;
import net.mmogroup.mmolib.api.item.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import static net.Indyuce.mmoitems.api.event.PlayerUseCraftingStationEvent.StationAction;

public class MMOItemsCraftObjective extends Objective implements Listener {

    private final Type itemType;
    private final String itemId;

    private final String recipeID;

    private final int amount;

    public MMOItemsCraftObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        template = CraftData.class;

        final TypeManager typeManager = MMOItems.plugin.getTypes();
        itemType = typeManager.get(instruction.next());
        itemId = instruction.next();

        recipeID = instruction.getOptional("recipeID");

        amount = instruction.getInt(instruction.next(), 1);
    }

    @EventHandler(ignoreCancelled = true)
    public void onRecipeUse(final CraftMMOItemEvent event) {
        final String playerID = PlayerConverter.getID(event.getPlayer());

        if (!containsPlayer(playerID) && !checkConditions(playerID)) {
            return;
        }
        final ItemStack item = event.getResult();
        final NBTItem realItemNBT = NBTItem.get(item);
        final String realItemType = realItemNBT.getString("MMOITEMS_ITEM_TYPE");
        final String realItemID = realItemNBT.getString("MMOITEMS_ITEM_ID");

        if (realItemID.equalsIgnoreCase(itemId) && realItemType.equalsIgnoreCase(itemType.getId())) {
            final CraftData playerData = (CraftData) dataMap.get(playerID);

            playerData.craftOne();
            if (playerData.isCompleted()) {
                completeObjective(playerID);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onRecipeUse(final PlayerUseCraftingStationEvent event) {
        final String playerID = PlayerConverter.getID(event.getPlayer());
        if (!containsPlayer(playerID) && !checkConditions(playerID)) {
            return;
        }
        if (!event.getRecipe().getId().equalsIgnoreCase(recipeID) && !(event.getInteraction() == StationAction.CRAFTING_QUEUE)) {
            return;
        }

        final CraftData playerData = (CraftData) dataMap.get(playerID);

        playerData.craftOne();
        if (playerData.isCompleted()) {
            completeObjective(playerID);
        }
    }

    public static class CraftData extends ObjectiveData {

        private int itemsLeft;

        public CraftData(final String instruction, final String playerID, final String objID) {
            super(instruction, playerID, objID);
            itemsLeft = Integer.parseInt(instruction);
        }

        private void craftOne() {
            itemsLeft--;
            update();
        }

        private boolean isCompleted() {
            return itemsLeft <= 0;
        }

        @Override
        public String toString() {
            return Integer.toString(itemsLeft);
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

    @Override
    public String getDefaultDataInstruction() {
        return String.valueOf(amount);
    }

    @Override
    public String getProperty(final String name, final String playerID) {
        return "";
    }
}


