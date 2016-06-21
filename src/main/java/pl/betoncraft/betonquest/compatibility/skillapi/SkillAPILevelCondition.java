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
package pl.betoncraft.betonquest.compatibility.skillapi;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerData;

import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.QuestRuntimeException;
import pl.betoncraft.betonquest.VariableNumber;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Checks the level of the player in SkillAPI.
 * 
 * @author Jakub Sapalski
 */
public class SkillAPILevelCondition extends Condition {

	private String className;
	private VariableNumber level;

	public SkillAPILevelCondition(String packName, String instructions) throws InstructionParseException {
		super(packName, instructions);
		String[] parts = instructions.split(" ");
		if (parts.length < 3) {
			throw new InstructionParseException("Not enough arguments");
		}
		className = parts[1];
		if (!SkillAPI.isClassRegistered(className)) {
			throw new InstructionParseException("Class '" + className + "' is not registered");
		}
		try {
			level = new VariableNumber(packName, parts[2]);
		} catch (NumberFormatException e) {
			throw new InstructionParseException("Could not parse level");
		}
	}

	@Override
	public boolean check(String playerID) throws QuestRuntimeException {
		PlayerData data = SkillAPI.getPlayerData(PlayerConverter.getPlayer(playerID));
		PlayerClass playerClass = data.getClass(className);
		return playerClass != null && level.getInt(playerID) <= playerClass.getLevel();
	}

}
