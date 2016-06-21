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

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.QuestRuntimeException;
import pl.betoncraft.betonquest.VariableNumber;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Gives the player specified potion effect
 * 
 * @author Jakub Sapalski
 */
public class EffectEvent extends QuestEvent {

	private final PotionEffectType effect;
	private final VariableNumber duration;
	private final VariableNumber amplifier;
	private final boolean ambient;

	public EffectEvent(String packName, String instructions) throws InstructionParseException {
		super(packName, instructions);
		String[] parts = instructions.split(" ");
		if (parts.length < 4) {
			throw new InstructionParseException("Not enough arguments");
		}
		effect = PotionEffectType.getByName(parts[1]);
		if (effect == null) {
			throw new InstructionParseException("Effect type does not exist");
		}
		try {
			duration = new VariableNumber(packName, parts[2]);
			amplifier = new VariableNumber(packName, parts[3]);
		} catch (NumberFormatException e) {
			throw new InstructionParseException("Could not parse number arguments");
		}
		ambient = instructions.contains("--ambient");
	}

	@Override
	public void run(String playerID) throws QuestRuntimeException {
		PlayerConverter.getPlayer(playerID).addPotionEffect(
				new PotionEffect(effect, duration.getInt(playerID) * 20, amplifier.getInt(playerID) - 1, ambient));
	}

}
