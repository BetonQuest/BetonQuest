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
package pl.betoncraft.betonquest.events;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.VariableNumber;
import pl.betoncraft.betonquest.api.QuestEvent;

/**
 * Clears all specified monsters in a certain location
 * 
 * @author Jakub Sapalski
 */
public class ClearEvent extends QuestEvent {

	private final EntityType[] types;
	private final Location location;
	private final VariableNumber range;
	private final String name;
	private final boolean kill;

	public ClearEvent(String packName, String instructions) throws InstructionParseException {
		super(packName, instructions);
		staticness = true;
		String[] parts = instructions.split(" ");
		if (parts.length < 3) {
			throw new InstructionParseException("Not enough arguments");
		}
		if (parts[1].equalsIgnoreCase("any") || parts[1].equalsIgnoreCase("all")) {
			types = null;
		} else {
			String[] rawTypes = parts[1].split(",");
			EntityType[] tempTypes = new EntityType[rawTypes.length];
			for (int i = 0; i < rawTypes.length; i++) {
				try {
					tempTypes[i] = EntityType.valueOf(rawTypes[i].toUpperCase());
				} catch (IllegalArgumentException e) {
					throw new InstructionParseException("Unknown mob type: " + rawTypes[i]);
				}
			}
			types = tempTypes;
		}
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
		boolean tempKill = false;
		for (String part : parts) {
			if (part.startsWith("name:")) {
				tempName = part.substring(5).replace("_", " ").trim();
			} else if (part.equalsIgnoreCase("kill")) {
				tempKill = true;
			}
		}
		name = tempName;
		kill = tempKill;
	}

	@Override
	public void run(String playerID) {
		Collection<Entity> entities = location.getWorld().getEntities();
		for (Entity entity : entities) {
			if (!(entity instanceof LivingEntity)) {
				continue;
			}
			double rangeDouble = range.getDouble(playerID);
			if (entity.getLocation().distanceSquared(location) < rangeDouble * rangeDouble) {
				EntityType theType = entity.getType();
				for (EntityType type : types) {
					if (theType == type) {
						if (name != null) {
							if (entity.getCustomName() != null && entity.getCustomName().equals(name)) {
								if (kill) {
									LivingEntity living = (LivingEntity) entity;
									living.damage(living.getHealth() + 10);
								} else {
									entity.remove();
								}
							}
						} else {
							if (kill) {
								LivingEntity living = (LivingEntity) entity;
								living.damage(living.getHealth() + 10);
							} else {
								entity.remove();
							}
						}
						break;
					}
				}
			}
		}
	}

}
