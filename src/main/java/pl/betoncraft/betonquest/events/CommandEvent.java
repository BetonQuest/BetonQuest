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

import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Fires a list of commands for the player
 * 
 * @author Jakub Sapalski
 */
public class CommandEvent extends QuestEvent {

    private final String[] commands;

    public CommandEvent(String packName, String instructions)
            throws InstructionParseException {
        super(packName, instructions);
        staticness = true;
        persistent = true;
        try {
            commands = instructions.trim()
                    .substring(instructions.indexOf(" ") + 1).split("\\|");
        } catch (Exception e) {
            throw new InstructionParseException("Could not parse commands");
        }
    }

    @Override
    public void run(String playerID) {
        for (String command : commands) {
            if (playerID == null) {
                if (command.contains("%player%")) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        Bukkit.getServer().dispatchCommand(
                                Bukkit.getConsoleSender(), command.replaceAll(
                                        "%player%", player.getName()));
                    }
                } else {
                    Bukkit.getServer().dispatchCommand(
                            Bukkit.getConsoleSender(), command);
                }
            } else {
                Bukkit.getServer().dispatchCommand(
                        Bukkit.getConsoleSender(),
                        command.replaceAll(
                                "%player%", PlayerConverter.getName(playerID)));
            }
        }
    }
}
