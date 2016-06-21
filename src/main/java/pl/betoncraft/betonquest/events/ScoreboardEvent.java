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
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.QuestRuntimeException;
import pl.betoncraft.betonquest.VariableNumber;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Adds/removes/multiplies/divides scores on scoreboards.
 * 
 * @author Jakub Sapalski
 */
public class ScoreboardEvent extends QuestEvent {

	final VariableNumber count;
	final boolean multi;
	final String objective;

	public ScoreboardEvent(String packName, String instructions) throws InstructionParseException {
		super(packName, instructions);
		persistent = true;
		String[] parts = instructions.split(" ");
		if (parts.length < 3) {
			throw new InstructionParseException("Not enough arguments");
		}
		objective = parts[1];
		if (parts[2].startsWith("*")) {
			multi = true;
			parts[2] = parts[2].replace("*", "");
		} else {
			multi = false;
		}
		try {
			count = new VariableNumber(packName, parts[2]);
		} catch (NumberFormatException e) {
			throw new InstructionParseException("Could not parse score count");
		}
	}

	@Override
	public void run(String playerID) throws IllegalStateException, QuestRuntimeException {
		Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
		Objective obj = board.getObjective(objective);
		if (obj == null) {
			throw new QuestRuntimeException("Scoreboard objective " + objective + " does not exist!");
		}
		Score score = obj.getScore(PlayerConverter.getName(playerID));
		if (multi) {
			score.setScore((int) Math.floor(score.getScore() * count.getDouble(playerID)));
		} else {
			score.setScore((int) Math.floor(score.getScore() + count.getDouble(playerID)));
		}
	}

}
