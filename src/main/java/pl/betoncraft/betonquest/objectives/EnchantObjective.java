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
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.QuestItem;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Requires the player to enchant an item.
 * 
 * @author Jakub Sapalski
 */
public class EnchantObjective extends Objective implements Listener {

	private QuestItem item;
	private Enchantment enchant;
	private int level;

	public EnchantObjective(String packName, String label, String instructions) throws InstructionParseException {
		super(packName, label, instructions);
		template = ObjectiveData.class;
		String[] parts = instructions.split(" ");
		if (parts.length < 3)
			throw new InstructionParseException("Not enough arguments");
		String itemInstruction = pack.getString("items." + parts[1]);
		if (itemInstruction == null) {
			throw new InstructionParseException("Item not defined: " + parts[1]);
		}
		item = new QuestItem(itemInstruction);
		String[] enchantParts = parts[2].split(":");
		if (enchantParts.length != 2)
			throw new InstructionParseException("Could not parse enchantment");
		enchant = Enchantment.getByName(enchantParts[0].toUpperCase());
		if (enchant == null)
			throw new InstructionParseException("Enchantment type '" + enchantParts[0] + "' does not exist");
		try {
			level = Integer.parseInt(enchantParts[1]);
		} catch (NumberFormatException e) {
			throw new InstructionParseException("Could not parse enchantment level");
		}
	}

	@EventHandler
	public void onEnchant(EnchantItemEvent event) {
		String playerID = PlayerConverter.getID(event.getEnchanter());
		if (!containsPlayer(playerID))
			return;
		if (!item.equalsI(event.getItem()))
			return;
		for (Enchantment enchant : event.getEnchantsToAdd().keySet())
			if (enchant == this.enchant)
				if (event.getEnchantsToAdd().get(enchant) >= level)
					if (checkConditions(playerID)) {
						completeObjective(playerID);
						return;
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
		return "";
	}

}
