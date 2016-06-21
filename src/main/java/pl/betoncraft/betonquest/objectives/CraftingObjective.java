/**
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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.QuestItem;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Player has to craft specified amount of items
 * 
 * @author Jakub Sapalski
 */
public class CraftingObjective extends Objective implements Listener {

	private final QuestItem item;
	private final int amount;

	public CraftingObjective(String packName, String label, String instruction) throws InstructionParseException {
		super(packName, label, instruction);
		template = CraftData.class;
		String[] parts = instructions.split(" ");
		if (parts.length < 3) {
			throw new InstructionParseException("Not enough arguments");
		}
		item = QuestItem.newQuestItem(packName, parts[1]);
		try {
			amount = Integer.parseInt(parts[2]);
		} catch (NumberFormatException e) {
			throw new InstructionParseException("Could not parse amount");
		}
		if (amount < 1) {
			throw new InstructionParseException("Amount cannot be less than 1");
		}
	}

	@EventHandler
	public void onCrafting(CraftItemEvent event) {
		if (event.getWhoClicked() instanceof Player) {
			Player player = (Player) event.getWhoClicked();
			String playerID = PlayerConverter.getID(player);
			CraftData playerData = (CraftData) dataMap.get(playerID);
			if (containsPlayer(playerID) && item.equalsI(event.getRecipe().getResult()) && checkConditions(playerID)) {
				playerData.subtract(event.getRecipe().getResult().getAmount());
				if (playerData.isZero()) {
					completeObjective(playerID);
				}
			}
		}
	}

	@EventHandler
	public void onShiftCrafting(InventoryClickEvent event) {
		if ((event.getInventory().getType().equals(InventoryType.CRAFTING) && event.getRawSlot() == 9)
				|| (event.getInventory().getType().equals(InventoryType.WORKBENCH) && event.getRawSlot() == 8)) {
			if (event.getClick().equals(ClickType.SHIFT_LEFT) && event.getWhoClicked() instanceof Player) {
				Player player = (Player) event.getWhoClicked();
				String playerID = PlayerConverter.getID(player);
				if (containsPlayer(playerID)) {
					event.setCancelled(true);
				}
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
	public String getProperty(String name, String playerID) {
		if (name.equalsIgnoreCase("left")) {
			return Integer.toString(amount - ((CraftData) dataMap.get(playerID)).getAmount());
		} else if (name.equalsIgnoreCase("amount")) {
			return Integer.toString(((CraftData) dataMap.get(playerID)).getAmount());
		}
		return "";
	}

	public static class CraftData extends ObjectiveData {

		private int amount;

		public CraftData(String instruction, String playerID, String objID) {
			super(instruction, playerID, objID);
			amount = Integer.parseInt(instruction);
		}

		private void subtract(int amount) {
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
