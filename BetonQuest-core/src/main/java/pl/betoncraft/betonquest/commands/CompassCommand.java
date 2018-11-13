/*
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
package pl.betoncraft.betonquest.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.Backpack;
import pl.betoncraft.betonquest.Backpack.DisplayType;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * The /compass command. It opens the list of quests.
 *
 * @author Jakub Sapalski
 */
public class CompassCommand implements CommandExecutor {

    /**
     * Registers a new executor of the /compass command
     */
    public CompassCommand() {
        BetonQuest.getInstance().getCommand("compass").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("compass")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                String playerID = PlayerConverter.getID(player);
                new Backpack(playerID, DisplayType.COMPASS);
            }
            return true;
        }
        return false;
    }
}
