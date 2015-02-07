/**
 * BetonQuest - advanced quests for Bukkit
 * Copyright (C) 2015  Jakub "Co0sh" Sapalski
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
package pl.betoncraft.betonquest.conditions;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import pl.betoncraft.betonquest.core.Condition;
import pl.betoncraft.betonquest.inout.PlayerConverter;

public class ArmorRatingCondition extends Condition {
	
	private int rating = 0;
	private int required;

	public ArmorRatingCondition(String playerID, String instructions) {
		super(playerID, instructions);
		
		String[] parts = instructions.split(" ");
		
		for (String part : parts) {
			if (part.contains("rating:")) {
				required = Integer.parseInt(part.split(":")[1]);
			}
		}
		
		PlayerInventory inv = PlayerConverter.getPlayer(playerID).getInventory();
	    ItemStack boots = inv.getBoots();
	    ItemStack helmet = inv.getHelmet();
	    ItemStack chest = inv.getChestplate();
	    ItemStack pants = inv.getLeggings();
	    if(helmet.getType() == Material.LEATHER_HELMET)rating += 1;
	    else if(helmet.getType() == Material.GOLD_HELMET)rating += 2;
	    else if(helmet.getType() == Material.CHAINMAIL_HELMET)rating += 2;
	    else if(helmet.getType() == Material.IRON_HELMET)rating += 2;
	    else if(helmet.getType() == Material.DIAMOND_HELMET)rating += 3;
	    //
	    if(boots.getType() == Material.LEATHER_BOOTS)rating += 1;
	    else if(boots.getType() == Material.GOLD_BOOTS)rating += 1;
	    else if(boots.getType() == Material.CHAINMAIL_BOOTS)rating += 1;
	    else if(boots.getType() == Material.IRON_BOOTS)rating += 2;
	    else if(boots.getType() == Material.DIAMOND_BOOTS)rating += 3;
	    //
	    if(pants.getType() == Material.LEATHER_LEGGINGS)rating += 2;
	    else if(pants.getType() == Material.GOLD_LEGGINGS)rating += 3;
	    else if(pants.getType() == Material.CHAINMAIL_LEGGINGS)rating += 4;
	    else if(pants.getType() == Material.IRON_LEGGINGS)rating += 5;
	    else if(pants.getType() == Material.DIAMOND_LEGGINGS)rating += 6;
	    //
	    if(chest.getType() == Material.LEATHER_CHESTPLATE)rating += 3;
	    else if(chest.getType() == Material.GOLD_CHESTPLATE)rating += 5;
	    else if(chest.getType() == Material.CHAINMAIL_CHESTPLATE)rating += 5;
	    else if(chest.getType() == Material.IRON_CHESTPLATE)rating += 6;
	    else if(chest.getType() == Material.DIAMOND_CHESTPLATE)rating += 8;
	}
	
	@Override
	public boolean isMet() {
		if (rating >= required) {
			return true;
		}
		return false;
	}

}
