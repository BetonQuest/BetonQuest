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
package pl.betoncraft.betonquest.core;

import org.bukkit.Bukkit;

import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Utility for sending messages to the player
 * 
 * @author Co0sh
 */
public class SimpleTextOutput {

    /**
     * Sends player a message that looks like NPC said it. All "%player%" in
     * message are replaced with player's name, all "%quester%" are replaced by
     * quester's name
     * 
     * @param playerID
     *            ID of the player
     * @param quester
     *            name of the NPC
     * @param message
     *            message
     */
    public static void sendQuesterMessage(String playerID, String quester, String message) {
        String finalString = (Config.getString("messages.global.quester_line_format") + message)
                .replaceAll("%player%", PlayerConverter.getPlayer(playerID).getName())
                .replaceAll("%quester%", quester).replaceAll("&", "§");
        PlayerConverter.getPlayer(playerID).sendMessage(finalString);
    }

    /**
     * Sends player a message that looks like an option to reply to NPC.
     * 
     * @param playerID
     *            ID of the player
     * @param number
     *            number of the option
     * @param quester
     *            name of the NPC
     * @param message
     *            message
     */
    public static void sendQuesterReply(String playerID, int number, String quester, String message, String hash) {
        String finalString = (Config.getString("messages.global.quester_reply_format") + message)
                .replaceAll("%quester%", quester).replaceAll("%number%", String.valueOf(number))
                .replaceAll("%player%", PlayerConverter.getPlayer(playerID).getName())
                .replaceAll("&", "§");
        if (Config.getString("config.tellraw").equalsIgnoreCase("true")) {
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),
                    "tellraw " + PlayerConverter.getPlayer(playerID).getName()
                        + " {\"text\":\"\",\"extra\":[{\"text\":\"" + finalString
                        + "\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\""
                        + "/betonquestanswer " + hash + "\"}}]}");
        } else {
            PlayerConverter.getPlayer(playerID).sendMessage(finalString);
        }
    }

    /**
     * Sends player a message that looks like his answer to NPC.
     * 
     * @param playerID
     *            ID of the player
     * @param quester
     *            name of the NPC
     * @param message
     */
    public static void sendPlayerReply(String playerID, String quester, String message) {
        String finalString = (Config.getString("messages.global.player_reply_format") + message)
                .replaceAll("%player%", PlayerConverter.getPlayer(playerID).getName())
                .replaceAll("%quester%", quester).replaceAll("&", "§");
        PlayerConverter.getPlayer(playerID).sendMessage(finalString);
    }
}
