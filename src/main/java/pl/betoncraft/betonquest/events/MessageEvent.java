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
import pl.betoncraft.betonquest.utils.Debug;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * 
 * @author Co0sh
 */
public class MessageEvent extends QuestEvent {

    /**
     * Constructor method
     * 
     * @param playerID
     * @param instructions
     */
    public MessageEvent(String playerID, String instructions) {
        super(playerID, instructions);
        // the event cannot be fired for offline players
        if (playerID != null && PlayerConverter.getPlayer(playerID) == null) {
            Debug.info("Player " + playerID + " is offline, cannot fire event");
            return;
        }
        String message = super.instructions
                .substring(super.instructions.split(" ")[0].length() + 1);
        if (playerID == null) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage(message.replaceAll("&", "§").replaceAll("%player%",
                        player.getName()));
            }
        } else {
            PlayerConverter.getPlayer(playerID).sendMessage(
                    message.replaceAll("&", "§").replaceAll("%player%",
                    PlayerConverter.getPlayer(playerID).getName()));
        }
    }

}
