package org.betonquest.betonquest.objectives;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.utils.BlockSelector;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

/**
 * Requires the player to smelt some amount of items
 */
@SuppressWarnings("PMD.CommentRequired")
public class SmeltingObjective extends Objective implements Listener {

    private final BlockSelector blockSelector;
    private final int amount;

    public SmeltingObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        template = SmeltData.class;
        blockSelector = new BlockSelector(instruction.next());
        amount = instruction.getInt();
        if (amount <= 0) {
            throw new InstructionParseException("Amount cannot be less than 1");
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onSmelting(final FurnaceExtractEvent event) {
        final String playerID = PlayerConverter.getID(event.getPlayer());
        if (containsPlayer(playerID) && blockSelector.match(event.getItemType()) && checkConditions(playerID)) {
            final SmeltData playerData = (SmeltData) dataMap.get(playerID);
            playerData.subtract(event.getItemAmount());
            if (playerData.isZero()) {
                completeObjective(playerID);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onShiftSmelting(final InventoryClickEvent event) {
        if (event.getInventory().getType().equals(InventoryType.FURNACE) && event.getRawSlot() == 2
                && event.getClick().equals(ClickType.SHIFT_LEFT) && event.getWhoClicked() instanceof Player) {
            final String playerID = PlayerConverter.getID((Player) event.getWhoClicked());
            if (containsPlayer(playerID)) {
                event.setCancelled(true);
            }
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
        if ("left".equalsIgnoreCase(name)) {
            return Integer.toString(amount - ((SmeltData) dataMap.get(playerID)).getAmount());
        } else if ("amount".equalsIgnoreCase(name)) {
            return Integer.toString(((SmeltData) dataMap.get(playerID)).getAmount());
        }
        return "";
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
