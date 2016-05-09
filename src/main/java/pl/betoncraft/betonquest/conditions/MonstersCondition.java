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

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.VariableNumber;
import pl.betoncraft.betonquest.api.Condition;

/**
 * Checks if there are specified monsters in the area
 * 
 * @author Jakub Sapalski
 */
public class MonstersCondition extends Condition {

	private final EntityType[] types;
	private final VariableNumber[] amounts;
	private final Location location;
	private final VariableNumber range;
	private final String name;

	public MonstersCondition(String packName, String instructions) throws InstructionParseException {
		super(packName, instructions);
		staticness = true;
		persistent = true;
		String[] parts = instructions.split(" ");
		if (parts.length < 3) {
			throw new InstructionParseException("Not enough arguments");
		}
		String[] rawTypes = parts[1].split(",");
		EntityType[] tempTypes = new EntityType[rawTypes.length];
		VariableNumber[] tempAmounts = new VariableNumber[rawTypes.length];
		for (int i = 0; i < rawTypes.length; i++) {
			try {
				if (rawTypes[i].contains(":")) {
					String[] typeParts = rawTypes[i].split(":");
					if (typeParts.length == 0) {
						throw new InstructionParseException("Type not defined");
					} else if (typeParts.length < 2) {
						tempTypes[i] = EntityType.valueOf(typeParts[0].toUpperCase());
						tempAmounts[i] = new VariableNumber(1);
					} else {
						tempTypes[i] = EntityType.valueOf(typeParts[0].toUpperCase());
						try {
							tempAmounts[i] = new VariableNumber(packName, typeParts[1]);
						} catch (NumberFormatException e) {
							throw new InstructionParseException("Could not parse amount");
						}
					}
				} else {
					tempTypes[i] = EntityType.valueOf(rawTypes[i].toUpperCase());
					tempAmounts[i] = new VariableNumber(1);
				}
			} catch (IllegalArgumentException e) {
				throw new InstructionParseException("Unknown mob type: " + rawTypes[i]);
			}
		}
		types = tempTypes;
		amounts = tempAmounts;
		String[] partsOfLoc = parts[2].split(";");
		if (partsOfLoc.length != 5) {
			throw new InstructionParseException("Wrong location format");
		}
		World world = Bukkit.getWorld(partsOfLoc[3]);
		if (world == null) {
			throw new InstructionParseException("World " + partsOfLoc[3] + " does not exists.");
		}
		double x, y, z;
		try {
			x = Double.parseDouble(partsOfLoc[0]);
			y = Double.parseDouble(partsOfLoc[1]);
			z = Double.parseDouble(partsOfLoc[2]);
		} catch (NumberFormatException e) {
			throw new InstructionParseException("Could not parse location coordinates");
		}
		location = new Location(world, x, y, z);
		try {
			range = new VariableNumber(packName, partsOfLoc[4]);
		} catch (NumberFormatException e) {
			throw new InstructionParseException("Could not parse range");
		}
		String tempName = null;
		for (String part : parts) {
			if (part.startsWith("name:")) {
				tempName = part.substring(5).replace("_", " ").trim();
				break;
			}
		}
		name = tempName;
	}

	@Override
	public boolean check(String playerID) {
		int[] neededAmounts = new int[types.length];
		for (int i = 0; i < neededAmounts.length; i++) {
			neededAmounts[i] = 0;
		}
		Collection<Entity> entities = location.getWorld().getEntities();
		double r = range.getDouble(playerID);
		for (Entity entity : entities) {
			if (!(entity instanceof LivingEntity)) {
				continue;
			}
			if (entity.getLocation().distanceSquared(location) < r * r) {
				EntityType theType = entity.getType();
				for (int i = 0; i < types.length; i++) {
					if (theType == types[i]) {
						if (name != null) {
							if (entity.getCustomName() != null && entity.getCustomName().equals(name)) {
								neededAmounts[i]++;
							}
						} else {
							neededAmounts[i]++;
						}
						break;
					}
				}
			}
		}
		for (int i = 0; i < amounts.length; i++) {
			if (neededAmounts[i] < amounts[i].getInt(playerID)) {
				return false;
			}
		}
		return true;
	}

}
