/*
 * BetonQuest - advanced quests for Bukkit
 * Copyright (C) 2016  Jakub "Co0sh" Sapalski
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package pl.betoncraft.betonquest.objectives;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Requires the player to smelt some amount of items
 *
 * @author Jakub Sapalski
 */
public class SmeltingObjective extends Objective implements Listener {

    private final Material material;
    private final int amount;

    public SmeltingObjective(Instruction instruction) throws InstructionParseException {
        super(instruction);
        template = SmeltData.class;
        material = instruction.getEnum(Material.class);
        amount = instruction.getInt();
        if (amount < 1) {
            throw new InstructionParseException("Amount cannot be less than 1");
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onSmelting(FurnaceExtractEvent event) {
        String playerID = PlayerConverter.getID(event.getPlayer());
        if (containsPlayer(playerID) && event.getItemType().equals(material) && checkConditions(playerID)) {
            SmeltData playerData = (SmeltData) dataMap.get(playerID);
            playerData.subtract(event.getItemAmount());
            if (playerData.isZero()) {
                completeObjective(playerID);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onShiftSmelting(InventoryClickEvent event) {
        if (event.getInventory().getType().equals(InventoryType.FURNACE) && event.getRawSlot() == 2
                && event.getClick().equals(ClickType.SHIFT_LEFT) && event.getWhoClicked() instanceof Player) {
            String playerID = PlayerConverter.getID((Player) event.getWhoClicked());
            if (containsPlayer(playerID))
                event.setCancelled(true);
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
    public String getProperty(String name, String playerID) {
        if (name.equalsIgnoreCase("left")) {
            return Integer.toString(amount - ((SmeltData) dataMap.get(playerID)).getAmount());
        } else if (name.equalsIgnoreCase("amount")) {
            return Integer.toString(((SmeltData) dataMap.get(playerID)).getAmount());
        }
        return "";
    }

    public static class SmeltData extends ObjectiveData {

        private int amount;

        public SmeltData(String instruction, String playerID, String objID) {
            super(instruction, playerID, objID);
            amount = Integer.parseInt(instruction);
        }

        private int getAmount() {
            return amount;
        }

        private void subtract(int amount) {
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
