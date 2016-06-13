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

import org.bukkit.scheduler.BukkitRunnable;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.database.PlayerData;
import pl.betoncraft.betonquest.utils.Debug;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Starts an objective for the player
 * 
 * @author Jakub Sapalski
 */
public class ObjectiveEvent extends QuestEvent {

	private final String objective;
	private final String action;

	public ObjectiveEvent(String packName, String instructions) throws InstructionParseException {
		super(packName, instructions);
		String[] parts = instructions.split(" ");
		if (parts.length < 3) {
			throw new InstructionParseException("Not enough arguments");
		}
		action = parts[1];
		if (!parts[2].contains(".")) {
			objective = packName + "." + parts[2];
		} else {
			objective = parts[2];
		}
		if (!action.equalsIgnoreCase("start") && !action.equalsIgnoreCase("delete")
				&& !action.equalsIgnoreCase("complete")) {
			throw new InstructionParseException("Unknown action " + action);
		}
		if (action.equalsIgnoreCase("complete")) {
			persistent = false;
		} else {
			persistent = true;
		}
	}

	@Override
	public void run(final String playerID) {
		if (BetonQuest.getInstance().getObjective(objective) == null) {
			Debug.error("Objective '" + objective + "' is not defined, cannot run objective event");
			return;
		}
		if (PlayerConverter.getPlayer(playerID) == null) {
			new BukkitRunnable() {
				@Override
				public void run() {
					PlayerData playerData = new PlayerData(playerID);
					if (action.equals("start")) {
						playerData.addNewRawObjective(objective);
					} else if (action.equals("delete")) {
						playerData.removeRawObjective(objective);
					} else {
						Debug.error("Cannot complete objective for offline player!");
					}
				}
			}.runTaskAsynchronously(BetonQuest.getInstance());
		} else {
			if (action.equalsIgnoreCase("start")) {
				BetonQuest.newObjective(playerID, objective);
			} else if (action.equalsIgnoreCase("complete")) {
				BetonQuest.getInstance().getObjective(objective).completeObjective(playerID);
			} else {
				BetonQuest.getInstance().getObjective(objective).removePlayer(playerID);
				BetonQuest.getInstance().getPlayerData(playerID).removeRawObjective(objective);
			}
		}
	}
}
