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

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.core.InstructionParseException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Sends a message to the player
 * 
 * @author Jakub Sapalski
 */
public class MessageEvent extends QuestEvent {
    
    private final String message;

    public MessageEvent(String packName, String instructions)
            throws InstructionParseException {
        super(packName, instructions);
        staticness = true;
        try {
            message = super.instructions.substring(8);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new InstructionParseException("Message not defined");
        }
    }
    
    @Override
    public void run(String playerID) {
	if (playerID == null) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage(message.replaceAll("&", "§")
                        .replaceAll("%player%", player.getName()));
            }
        } else {
            PlayerConverter.getPlayer(playerID).sendMessage(
                    message.replaceAll("&", "§").replaceAll("%player%",
                    PlayerConverter.getPlayer(playerID).getName()));
        }
    }

}
