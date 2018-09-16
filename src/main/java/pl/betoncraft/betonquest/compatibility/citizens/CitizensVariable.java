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
package pl.betoncraft.betonquest.compatibility.citizens;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.api.Variable;

/**
 * Provides information about a citizen npc.
 *
 * Format:
 *   %citizen.<id>.<key>%
 *
 * Keys:
 *   * name - (default) Return citizen name
 *   * location  - Return citizen location. x;y;z;world;yaw;pitch
 */
public class CitizensVariable extends Variable {

	private int id;
	private String key;

	public CitizensVariable(Instruction instruction) throws InstructionParseException {
		super(instruction);

		String [] parts = instruction.getInstruction().split("\\.");
		if (parts.length < 2) {
			throw new InstructionParseException("Invalid variable format");
		}

		try {
			id = Integer.parseInt(parts[1]);
		} catch (NumberFormatException ex) {
			throw new InstructionParseException("Invalid NPC ID: " + parts[1]);
		}

		// Accept any key. We return blank if its invalid to support forward compatibility
		key = parts[2];
	}

	@Override
	public String getValue(String playerID) {
		NPC npc = CitizensAPI.getNPCRegistry().getById(id);
		if (npc == null) {
			return "";
		}

		switch(key) {
			case "name":
				return npc.getName();
			case "full_name":
				return npc.getFullName();
			case "location":
				if (npc.getEntity() != null) {
					Location loc = npc.getEntity().getLocation();
					return String.format("%.2f;%.2f;%.2f;%s;%.2f;%.2f",
							loc.getX(),
							loc.getY(),
							loc.getZ(),
							loc.getWorld().getName(),
							loc.getYaw(),
							loc.getPitch());
				}
				break;
		}
		return "";
	}

}
