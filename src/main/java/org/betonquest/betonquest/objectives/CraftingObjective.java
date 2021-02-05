package org.betonquest.betonquest.objectives;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.item.QuestItem;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Player has to craft specified amount of items
 */
@SuppressWarnings("PMD.CommentRequired")
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
            final int absoluteCreations = countPossibleCrafts(event);
            final int remainingSpace = countRemainingSpace(player);
            playerData.subtract(Math.min(remainingSpace, absoluteCreations));
            if (playerData.isZero()) {
                completeObjective(playerID);
            }
        }
    }

    private int countPossibleCrafts(final CraftItemEvent event) {
        int possibleCreations = 1;
        if (event.isShiftClick()) {
            possibleCreations = Integer.MAX_VALUE;
            for (final ItemStack item : event.getInventory().getMatrix()) {
                if (item != null && !item.getType().equals(Material.AIR)) {
                    possibleCreations = Math.min(possibleCreations, item.getAmount());
                }
            }
        }
        return possibleCreations * event.getRecipe().getResult().getAmount();
    }

    private int countRemainingSpace(final Player player) {
        int remainingSpace = 0;
        for (final ItemStack i : player.getInventory().getStorageContents()) {
            if (i == null || i.getType().equals(Material.AIR)) {
                remainingSpace += item.getMaterial().getMaxStackSize();
            } else if (i.equals(item.generate(i.getAmount()))) {
                remainingSpace += item.getMaterial().getMaxStackSize() - i.getAmount();
            }
        }
        return remainingSpace;
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
            return Integer.toString(amount - ((CraftData) dataMap.get(playerID)).getAmount());
        } else if ("amount".equalsIgnoreCase(name)) {
            return Integer.toString(((CraftData) dataMap.get(playerID)).getAmount());
        }
        return "";
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
