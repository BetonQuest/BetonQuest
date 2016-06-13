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
package pl.betoncraft.betonquest.conditions;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.utils.PlayerConverter;

public class VehicleCondition extends Condition {
	
	private EntityType vehicle;
	private boolean any;

	public VehicleCondition(String packName, String instructions) throws InstructionParseException {
		super(packName, instructions);
		String[] parts = instructions.split(" ");
		if (parts.length < 2) {
			throw new InstructionParseException("Not enough arguments");
		}
		if (parts[1].equalsIgnoreCase("any")) {
			any = true;
		} else try {
			vehicle = EntityType.valueOf(parts[1].toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new InstructionParseException("Entity type " + parts[1] + " does not exist.");
		}
	}

	@Override
	public boolean check(String playerID) {
		Entity entity = PlayerConverter.getPlayer(playerID).getVehicle();
		return entity != null && (any || entity.getType() == vehicle);
	}

}
