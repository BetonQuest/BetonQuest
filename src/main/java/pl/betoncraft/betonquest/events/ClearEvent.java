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
package pl.betoncraft.betonquest.events;

import java.util.Collection;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.QuestRuntimeException;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.utils.LocationData;

/**
 * Clears all specified monsters in a certain location
 * 
 * @author Jakub Sapalski
 */
public class ClearEvent extends QuestEvent {

	private final EntityType[] types;
	private final LocationData loc;
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
		loc = new LocationData(packName, parts[2]);
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
	public void run(String playerID) throws QuestRuntimeException {
		Location location = loc.getLocation(playerID);
		Collection<Entity> entities = location.getWorld().getEntities();
		for (Entity entity : entities) {
			if (!(entity instanceof LivingEntity)) {
				continue;
			}
			double rangeDouble = loc.getData().getDouble(playerID);
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
