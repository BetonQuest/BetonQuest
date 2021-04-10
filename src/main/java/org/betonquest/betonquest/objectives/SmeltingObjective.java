package org.betonquest.betonquest.objectives;

import lombok.CustomLog;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.utils.BlockSelector;
import org.betonquest.betonquest.utils.InventoryUtils;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Locale;

/**
 * Requires the player to smelt some amount of items.
 */
@SuppressWarnings("PMD.CommentRequired")
@CustomLog
public class SmeltingObjective extends Objective implements Listener {

    private final BlockSelector blockSelector;
    private final int amount;
    private final boolean notify;
    private final int notifyInterval;

    public SmeltingObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        template = SmeltData.class;
        blockSelector = new BlockSelector(instruction.next());
        amount = instruction.getInt();
        if (amount <= 0) {
            throw new InstructionParseException("Amount cannot be less than 1");
        }
        notifyInterval = instruction.getInt(instruction.getOptional("notify"), 1);
        notify = instruction.hasArgument("notify") || notifyInterval > 1;
    }

    @EventHandler(ignoreCancelled = true)
    public void onSmelting(final InventoryClickEvent event) {
        final InventoryType inventoryType = event.getInventory().getType();
        if (isSmeltingResultExtraction(event, inventoryType)) {
            final String playerID = PlayerConverter.getID((Player) event.getWhoClicked());
            assert event.getCurrentItem() != null;
            if (containsPlayer(playerID) && blockSelector.match(event.getCurrentItem().getType()) && checkConditions(playerID)) {
                progressSmeltingObjective(event, playerID);
            }
        }
    }

    private boolean isSmeltingResultExtraction(final InventoryClickEvent event, final InventoryType inventoryType) {
        return (inventoryType == InventoryType.FURNACE
                || inventoryType == InventoryType.SMOKER
                || inventoryType == InventoryType.BLAST_FURNACE)
                && event.getWhoClicked() instanceof Player
                && event.getRawSlot() == 2
                && !InventoryUtils.isEmptySlot(event.getCurrentItem());
    }

    private void progressSmeltingObjective(final InventoryClickEvent event, final String playerID) {
        final int taken = calculateTakeAmount(event);
        final SmeltData playerData = (SmeltData) dataMap.get(playerID);
        playerData.subtract(taken);
        if (playerData.isZero()) {
            completeObjective(playerID);
        } else if (notify && playerData.getAmount() % notifyInterval == 0) {
            sendNotify(playerID, "items_to_smelt", playerData);
        }
    }


    @SuppressWarnings("PMD.CyclomaticComplexity")
    private int calculateTakeAmount(final InventoryClickEvent event) {
        final ItemStack result = event.getCurrentItem();
        assert result != null;
        final PlayerInventory inventory = event.getWhoClicked().getInventory();
        switch (event.getClick()) {
            case SHIFT_LEFT:
            case SHIFT_RIGHT:
                return Math.min(InventoryUtils.calculateSpaceForItem(inventory, result), result.getAmount());
            case CONTROL_DROP:
                return InventoryUtils.calculateSpaceForItem(inventory, result);
            case NUMBER_KEY:
                return InventoryUtils.calculateSwapCraftAmount(result, inventory.getItem(event.getHotbarButton()));
            case SWAP_OFFHAND:
                return InventoryUtils.calculateSwapCraftAmount(result, inventory.getItemInOffHand());
            case DROP:
                return 1;
            case RIGHT:
                if (InventoryUtils.isEmptySlot(event.getCursor())) {
                    return (result.getAmount() + 1) / 2;
                }
            case LEFT:
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
                return Integer.toString(amount - ((SmeltData) dataMap.get(playerID)).getAmount());
            case "left":
                return Integer.toString(((SmeltData) dataMap.get(playerID)).getAmount());
            case "total":
                return Integer.toString(amount);
            default:
                return "";
        }
    }

    public static class SmeltData extends ObjectiveData {

        private int amount;

        public SmeltData(final String instruction, final String playerID, final String objID) {
            super(instruction, playerID, objID);
            amount = Integer.parseInt(instruction);
        }

        private int getAmount() {
            return amount;
        }

        private void subtract(final int amount) {
            this.amount -= amount;
            update();
        }

        private boolean isZero() {
            return amount <= 0;
        }

        @Override
        public String toString() {
            return Integer.toString(amount);
        }

    }

}
