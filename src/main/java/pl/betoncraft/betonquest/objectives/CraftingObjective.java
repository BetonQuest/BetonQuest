package pl.betoncraft.betonquest.objectives;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.item.QuestItem;
import pl.betoncraft.betonquest.utils.InventoryUtils;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.Locale;

/**
 * Player has to craft specified amount of items.
 */
@SuppressWarnings({"PMD.CommentRequired"})
public class CraftingObjective extends Objective implements Listener {

    private final QuestItem item;
    private final int amount;
    private final boolean notify;
    private final int notifyInterval;

    public CraftingObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        template = CraftData.class;
        item = instruction.getQuestItem();
        amount = instruction.getInt();
        if (amount <= 0) {
            throw new InstructionParseException("Amount cannot be less than 1");
        }
        notifyInterval = instruction.getInt(instruction.getOptional("notify"), 1);
        notify = instruction.hasArgument("notify") || notifyInterval > 1;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCrafting(final CraftItemEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        final Player player = (Player) event.getWhoClicked();
        final String playerID = PlayerConverter.getID(player);
        if (containsPlayer(playerID) && item.compare(event.getRecipe().getResult()) && checkConditions(playerID)) {
            progressCraftObjective(event, playerID);
        }
    }

    private void progressCraftObjective(final CraftItemEvent event, final String playerID) {
        final int crafted = calculateCraftAmount(event);
        final CraftData playerData = (CraftData) dataMap.get(playerID);
        playerData.subtract(crafted);
        if (playerData.isZero()) {
            completeObjective(playerID);
        } else if (notify && playerData.getAmount() % notifyInterval == 0) {
            sendNotify(playerID, "items_to_craft", playerData);
        }
    }

    private static int calculateCraftAmount(final CraftItemEvent event) {
        final ItemStack result = event.getRecipe().getResult();
        final PlayerInventory inventory = event.getWhoClicked().getInventory();
        final ItemStack[] ingredients = event.getInventory().getMatrix();
        switch (event.getClick()) {
            case SHIFT_LEFT:
            case SHIFT_RIGHT:
                return InventoryUtils.calculateShiftCraftAmount(result, inventory, ingredients);
            case CONTROL_DROP:
                return InventoryUtils.calculateMaximumCraftAmount(result, ingredients);
            case NUMBER_KEY:
                return InventoryUtils.calculateSwapCraftAmount(result, inventory.getItem(event.getHotbarButton()));
            case SWAP_OFFHAND:
                return InventoryUtils.calculateSwapCraftAmount(result, inventory.getItemInOffHand());
            case DROP:
                return result.getAmount();
            case LEFT:
            case RIGHT:
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

    @Override
    public String getDefaultDataInstruction() {
        return Integer.toString(amount);
    }

    @Override
    public String getProperty(final String name, final String playerID) {
        switch (name.toLowerCase(Locale.ROOT)) {
            case "amount":
                return Integer.toString(amount - ((CraftData) dataMap.get(playerID)).getAmount());
            case "left":
                return Integer.toString(((CraftData) dataMap.get(playerID)).getAmount());
            case "total":
                return Integer.toString(amount);
            default:
                return "";
        }
    }

    public static class CraftData extends ObjectiveData {

        private int amount;

        public CraftData(final String instruction, final String playerID, final String objID) {
            super(instruction, playerID, objID);
            amount = Integer.parseInt(instruction);
        }

        private void subtract(final int amount) {
            this.amount -= amount;
            update();
        }

        private boolean isZero() {
            return amount <= 0;
        }

        private int getAmount() {
            return amount;
        }

        @Override
        public String toString() {
            return String.valueOf(amount);
        }

    }

}
