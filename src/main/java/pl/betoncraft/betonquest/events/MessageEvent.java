/**
 * BetonQuest - advanced quests for Bukkit
 * Copyright (C) 2015  Jakub "Co0sh" Sapalski
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

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.core.InstructionParseException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Sends a message to the player, in his language
 * 
 * @author Jakub Sapalski
 */
public class MessageEvent extends QuestEvent {
    
    private final HashMap<String, String> messages = new HashMap<>();

    public MessageEvent(String packName, String instructions)
            throws InstructionParseException {
        super(packName, instructions);
        staticness = true;
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
    }
    
    @Override
    public void run(String playerID) {
	if (playerID == null) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                String lang = BetonQuest.getInstance().getDBHandler(
                        PlayerConverter.getID(player)).getLanguage();
                String message = messages.get(lang);
                if (message == null) {
                    messages.get(Config.getLanguage());
                }
                player.sendMessage(message.replaceAll("&", "§")
                        .replaceAll("%player%", player.getName()));
            }
        } else {
            String lang = BetonQuest.getInstance().getDBHandler(playerID)
                    .getLanguage();
            String message = messages.get(lang);
            if (message == null) {
                message = messages.get(Config.getLanguage());
            }
            PlayerConverter.getPlayer(playerID).sendMessage(
                    message.replaceAll("&", "§").replaceAll("%player%",
                    PlayerConverter.getName(playerID)));
        }
    }

}
