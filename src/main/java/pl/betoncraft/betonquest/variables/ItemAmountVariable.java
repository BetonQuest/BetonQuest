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
package pl.betoncraft.betonquest.variables;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.QuestItem;
import pl.betoncraft.betonquest.api.Variable;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Allows you to count items in player's inventory and display number remaining
 * to some amount.
 * 
 * @author Jakub Sapalski
 */
public class ItemAmountVariable extends Variable {

	private QuestItem questItem;
	private Type type;
	private int amount;

	public ItemAmountVariable(String packName, String instruction) throws InstructionParseException {
		super(packName, instruction);
		String[] parts = instruction.replace("%", "").split("\\.");
		if (parts.length != 3) {
			throw new InstructionParseException("Incorrect number of arguments");
		}
		String name = parts[1];
		String itemInstruction = pack.getString("items." + name);
		if (itemInstruction == null) {
			throw new InstructionParseException("Item not defined: " + name);
		}
		questItem = new QuestItem(itemInstruction);
		if (parts[2].toLowerCase().startsWith("left:")) {
			type = Type.LEFT;
			try {
				amount = Integer.parseInt(parts[2].substring(5));
			} catch (NumberFormatException e) {
				throw new InstructionParseException("Could not parse item amount");
			}
		} else if (parts[2].equalsIgnoreCase("amount")) {
			type = Type.AMOUNT;
		}
	}

	@Override
	public String getValue(String playerID) {
		Player player = PlayerConverter.getPlayer(playerID);
		int playersAmount = 0;
		for (ItemStack item : player.getInventory().getContents()) {
			if (item == null) {
				continue;
			}
			if (!questItem.equalsI(item)) {
				continue;
			}
			playersAmount += item.getAmount();
		}
		List<ItemStack> backpackItems = BetonQuest.getInstance().getPlayerData(playerID).getBackpack();
		for (ItemStack item : backpackItems) {
			if (item == null) {
				continue;
			}
			if (!questItem.equalsI(item)) {
				continue;
			}
			playersAmount += item.getAmount();
		}
		switch (type) {
		case AMOUNT:
			return Integer.toString(playersAmount);
		case LEFT:
			return Integer.toString(amount - playersAmount);
		default:
			return "";
		}
	}

	private enum Type {
		AMOUNT, LEFT
	}

}
