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
package pl.betoncraft.betonquest.commands;

import java.util.HashMap;
import java.util.Set;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.config.ConfigHandler;
import pl.betoncraft.betonquest.core.Conversation;
import pl.betoncraft.betonquest.utils.Debug;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Tellraw command handles the unique tellraw responses in conversations
 * 
 * @author Coosh
 */
public class TellrawCommand implements CommandExecutor {

    /**
     * Registers a new executor of the /betonquestanswer command
     */
    public TellrawCommand() {
        if (ConfigHandler.getString("config.tellraw").equalsIgnoreCase("true"))
            BetonQuest.getInstance().getCommand("betonquestanswer").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("betonquestanswer")) {
            if (args.length == 1 && sender instanceof Player) {
                Player player = (Player) sender;
                Conversation con = Conversation.getConversation(PlayerConverter.getID(player));
                if (con == null) return true;
                HashMap<Integer, String> hashes = con.getHashes();
                Set<Integer> set = hashes.keySet();
                for (Integer integer : set) {
                    if (hashes.get(integer).equals(args[0])) {
                        Debug.info("Passing player answer " + integer);
                        con.passPlayerAnswer(integer.toString());
                        return true;
                    }
                }
            }
            return true;
        }
        return false;
    }

}
