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
package pl.betoncraft.betonquest.compatibility.mythicmobs;

import org.bukkit.Location;

import net.elseland.xikage.MythicMobs.Mobs.MobSpawner;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.QuestRuntimeException;
import pl.betoncraft.betonquest.VariableNumber;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.utils.LocationData;

/**
 * Spawns MythicMobs mobs
 * 
 * @author Jakub Sapalski
 */
public class MythicSpawnMobEvent extends QuestEvent {

	private final LocationData loc;
	private final String mob;
	private final VariableNumber amount;
	private final VariableNumber level;

	public MythicSpawnMobEvent(String packName, String instructions) throws InstructionParseException {
		super(packName, instructions);
		String[] parts = instructions.split(" ");
		if (parts.length < 4) {
			throw new InstructionParseException("Not enough arguments");
		}
		loc = new LocationData(packName, parts[1]);
		String[] mobParts = parts[2].split(":");
		if (mobParts.length != 2) {
			throw new InstructionParseException("Wrong mob format");
		}
		mob = mobParts[0];
		try {
			level = new VariableNumber(packName, parts[2].split(":")[1]);
		} catch (NumberFormatException e) {
			throw new InstructionParseException("Could not parse mob level");
		}
		try {
			amount = new VariableNumber(packName, parts[3]);
		} catch (NumberFormatException e) {
			throw new InstructionParseException("Could not parse mob amount");
		}
	}

	@Override
	public void run(String playerID) throws QuestRuntimeException {
		int a = amount.getInt(playerID);
		int l = level.getInt(playerID);
		Location location = loc.getLocation(playerID);
		for (int i = 0; i < a; i++) {
			MobSpawner.SpawnMythicMob(mob, location, l);
		}
	}

}
