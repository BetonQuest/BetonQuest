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
package pl.betoncraft.betonquest.compatibility.effectlib;

import org.bukkit.configuration.ConfigurationSection;

import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.QuestRuntimeException;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.compatibility.Compatibility;
import pl.betoncraft.betonquest.utils.LocationData;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Displays an effect.
 * 
 * @author Jakub Sapalski
 */
public class ParticleEvent extends QuestEvent {

	private String effectClass;
	private ConfigurationSection parameters;
	private LocationData loc;

	public ParticleEvent(Instruction instruction) throws InstructionParseException {
		super(instruction);
		String string = instruction.next();
		parameters = instruction.getPackage().getCustom().getConfig().getConfigurationSection("effects." + string);
		if (parameters == null) {
			throw new InstructionParseException("Effect '" + string + "' does not exist!");
		}
		effectClass = parameters.getString("class");
		if (effectClass == null) {
			throw new InstructionParseException("Effect '" + string + "' is incorrectly defined");
		}
		loc = instruction.getLocation(instruction.getOptional("loc"));

	}

	@Override
	public void run(String playerID) throws QuestRuntimeException {
		if (loc == null) {
			Compatibility.getEffectManager().start(effectClass, parameters, PlayerConverter.getPlayer(playerID));
		} else {
			Compatibility.getEffectManager().start(effectClass, parameters, loc.getLocation(playerID));
		}
	}

}
