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

import pl.betoncraft.betonquest.core.Condition;
import pl.betoncraft.betonquest.inout.PlayerConverter;

/**
 * Checks Y height player is at (must be below)
 * @author BYK
 */
public class HeightCondition extends Condition {

	private double height;
	
	public HeightCondition(String playerID, String instructions) {
		super(playerID, instructions);
		String[] parts=instructions.split(" ");
		for (String part : parts) {
			if(part.contains("height:")){
				height = Double.parseDouble(part.substring(7));
			}
		}
	}

	@Override
	public boolean isMet() {
		if(PlayerConverter.getPlayer(playerID).getLocation().getY() <= height){
			return true;
		}
		return false;
	}

}
