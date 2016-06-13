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

import java.util.ArrayList;
import java.util.HashMap;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Sends a message to the player, in his language
 * 
 * @author Jakub Sapalski
 */
public class MessageEvent extends QuestEvent {

	private final HashMap<String, String> messages = new HashMap<>();
	private final ArrayList<String> variables = new ArrayList<>();

	public MessageEvent(String packName, String instructions) throws InstructionParseException {
		super(packName, instructions);
		String[] parts;
		try {
			parts = instructions.substring(8).split(" ");
		} catch (IndexOutOfBoundsException e) {
			throw new InstructionParseException("Message missing");
		}
		if (parts.length < 1) {
			throw new InstructionParseException("Message missing");
		}
		String currentLang = Config.getLanguage();
		StringBuilder string = new StringBuilder();
		for (String part : parts) {
			if (part.startsWith("conditions:")) {
				continue;
			} else if (part.matches("^\\{.+\\}$")) {
				if (string.length() > 0) {
					messages.put(currentLang, string.toString().trim());
					string = new StringBuilder();
				}
				currentLang = part.substring(1, part.length() - 1);
			} else {
				string.append(part + " ");
			}
		}
		if (string.length() > 0) {
			messages.put(currentLang, string.toString().trim());
		}
		if (messages.isEmpty()) {
			throw new InstructionParseException("Message missing");
		}
		for (String message : messages.values()) {
			for (String variable : BetonQuest.resolveVariables(message)) {
				BetonQuest.createVariable(pack, variable);
				if (!variables.contains(variable))
					variables.add(variable);
			}
		}
	}

	@Override
	public void run(String playerID) {
		String lang = BetonQuest.getInstance().getPlayerData(playerID).getLanguage();
		String message = messages.get(lang);
		if (message == null) {
			message = messages.get(Config.getLanguage());
		}
		for (String variable : variables) {
			message = message.replace(variable,
					BetonQuest.getInstance().getVariableValue(pack.getName(), variable, playerID));
		}
		PlayerConverter.getPlayer(playerID).sendMessage(message.replaceAll("&", "§"));
	}

}
