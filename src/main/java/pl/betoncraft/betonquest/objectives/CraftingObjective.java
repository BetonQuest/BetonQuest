package pl.betoncraft.betonquest.objectives;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.item.QuestItem;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

/**
 * Player has to craft specified amount of items.
 */
@SuppressWarnings({"PMD.CommentRequired", "PMD.TooManyMethods"})
public class CraftingObjective extends Objective implements Listener {

    private final QuestItem item;
    private final int amount;

    public CraftingObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        template = CraftData.class;
        item = instruction.getQuestItem();
        amount = instruction.getInt();
        if (amount <= 0) {
            throw new InstructionParseException("Amount cannot be less than 1");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCrafting(final CraftItemEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        final Player player = (Player) event.getWhoClicked();
        final String playerID = PlayerConverter.getID(player);
        final CraftData playerData = (CraftData) dataMap.get(playerID);
        if (containsPlayer(playerID) && item.compare(event.getRecipe().getResult()) && checkConditions(playerID)) {
            final int crafted = calculateCraftAmount(event);
            playerData.subtract(crafted);
            if (playerData.isZero()) {
                completeObjective(playerID);
            }
        }
    }

    private static int calculateCraftAmount(final CraftItemEvent event) {
        switch (event.getClick()) {
            case SHIFT_LEFT:
            case SHIFT_RIGHT:
                return calculateShiftCraftAmount(event);
            case CONTROL_DROP:
                return calculateMaximumCraftAmount(event);
            case NUMBER_KEY:
                return calculateHotbarCraftAmount(event);
            case SWAP_OFFHAND:
                return calculateOffhandCraftAmount(event);
            case DROP:
                return event.getRecipe().getResult().getAmount();
            case LEFT:
            case RIGHT:
                return calculateSimpleCraftAmount(event);
            default:
                return 0;
        }
    }

    private static int calculateShiftCraftAmount(final CraftItemEvent event) {
        final ItemStack craftResult = event.getRecipe().getResult();
        final int remainingSpace = calculateRemainingSpace(event.getWhoClicked(), craftResult);
        final int itemsPerCraft = craftResult.getAmount();
        final int spaceForCrafts = remainingSpace / itemsPerCraft;
        return Math.min(calculateMaximumCraftActions(event), spaceForCrafts) * itemsPerCraft;
    }

    private static int calculateMaximumCraftAmount(final CraftItemEvent event) {
        final int itemsPerCraft = event.getRecipe().getResult().getAmount();
        return calculateMaximumCraftActions(event) * itemsPerCraft;
    }

    private static int calculateMaximumCraftActions(final CraftItemEvent event) {
        return Arrays.stream(event.getInventory().getMatrix())
                .filter(Objects::nonNull)
                .filter(item -> item.getType() != Material.AIR)
                .mapToInt(ItemStack::getAmount)
                .min()
                .orElse(Integer.MAX_VALUE);
    }

    private static int calculateRemainingSpace(final HumanEntity player, final ItemStack crafted) {
        int remainingSpace = 0;
        for (final ItemStack i : player.getInventory().getStorageContents()) {
            if (isEmptySlot(i)) {
                remainingSpace += crafted.getMaxStackSize();
            } else if (i.isSimilar(crafted)) {
                remainingSpace += crafted.getMaxStackSize() - i.getAmount();
            }
        }
        return remainingSpace;
    }

    private static int calculateHotbarCraftAmount(final CraftItemEvent event) {
        assert event.getClick() == ClickType.NUMBER_KEY;
        final int hotbarSlot = event.getHotbarButton();
        final ItemStack hotbarItem = event.getWhoClicked().getInventory().getItem(hotbarSlot);
        return isEmptySlot(hotbarItem) ? event.getRecipe().getResult().getAmount() : 0;
    }

    private static int calculateOffhandCraftAmount(final CraftItemEvent event) {
        assert event.getClick() == ClickType.SWAP_OFFHAND;
        final ItemStack offhand = event.getWhoClicked().getInventory().getItemInOffHand();
        return isEmptySlot(offhand) ? event.getRecipe().getResult().getAmount() : 0;
    }


    private static int calculateSimpleCraftAmount(final CraftItemEvent event) {
        final ItemStack cursor = event.getCursor();
        final ItemStack result = event.getRecipe().getResult();
        if (isEmptySlot(cursor)
                || cursor.isSimilar(result) && cursor.getAmount() + result.getAmount() <= cursor.getMaxStackSize()) {
            return result.getAmount();
        }
        return 0;
    }

    private static boolean isEmptySlot(final ItemStack slotItem) {
        return slotItem == null || slotItem.getType().equals(Material.AIR);
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
