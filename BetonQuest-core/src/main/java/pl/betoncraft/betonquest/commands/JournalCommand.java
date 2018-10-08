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
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Gives the player his journal
 *
 * @author Jakub Sapalski
 */
public class JournalCommand implements CommandExecutor {

    /**
     * Registers a new executor of the /journal command
     */
    public JournalCommand() {
        BetonQuest.getInstance().getCommand("journal").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("journal")) {
            // command sender must be a player, console can't have journal
            if (sender instanceof Player) {
                // giving the player his journal
                BetonQuest.getInstance().getPlayerData(PlayerConverter.getID((Player) sender)).getJournal()
                        .addToInv(Integer.parseInt(Config.getString("config.default_journal_slot")));
            }
            return true;
        }
        return false;
    }

}
