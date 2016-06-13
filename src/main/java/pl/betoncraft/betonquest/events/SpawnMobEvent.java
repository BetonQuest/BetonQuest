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
 * Spawns mobs at given location
 * 
 * @author Jakub Sapalski
 */
public class SpawnMobEvent extends QuestEvent {

	private final Location loc;
	private final EntityType type;
	private final VariableNumber amount;
	private final String name;

	public SpawnMobEvent(String packName, String instructions) throws InstructionParseException {
		super(packName, instructions);
		staticness = true;
		String[] parts = instructions.split(" ");
		if (parts.length < 4) {
			throw new InstructionParseException("Not enough arguments");
		}
		String[] coords = parts[1].split(";");
		if (coords.length < 4) {
			throw new InstructionParseException("Wrong location format");
		}
		World world = Bukkit.getWorld(coords[3]);
		if (world == null) {
			throw new InstructionParseException("World does not exist");
		}
		double x, y, z;
		try {
			x = Double.parseDouble(coords[0]);
			y = Double.parseDouble(coords[1]);
			z = Double.parseDouble(coords[2]);
		} catch (NumberFormatException e) {
			throw new InstructionParseException("Could not parse coordinates");
		}
		loc = new Location(world, x, y, z);
		try {
			type = EntityType.valueOf(parts[2].toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new InstructionParseException("Entity type does not exist");
		}
		try {
			amount = new VariableNumber(packName, parts[3]);
		} catch (NumberFormatException e) {
			throw new InstructionParseException("Could not parse amount");
		}
		String tempName = null;
		for (String part : parts) {
			if (part.startsWith("name:")) {
				tempName = part.substring(5).replace("_", " ");
				break;
			}
		}
		name = tempName;
	}

	@Override
	public void run(String playerID) {
		int a = amount.getInt(playerID);
		for (int i = 0; i < a; i++) {
			Entity entity = loc.getWorld().spawnEntity(loc, type);
			if (name != null && entity instanceof LivingEntity) {
				LivingEntity livingEntity = (LivingEntity) entity;
				livingEntity.setCustomName(name);
			}
		}
	}
}
