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
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

/**
 * Interface which handles tab complete for commands.
 *
 * @author Jonas Blocher
 */
public interface SimpleTabCompleter extends TabCompleter {


    @Override
    default List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completations = this.simpleTabComplete(sender, command, alias, args);
        if (completations == null) return null;
        List<String> out = new ArrayList<>();
        String lastArg = args[args.length - 1];
        for (String completation : completations) {
            if (lastArg == null || lastArg.matches(" *") || completation.toLowerCase().startsWith(lastArg.toLowerCase())) {
                out.add(completation);
            }
        }
        return out;
    }

    List<String> simpleTabComplete(CommandSender sender, Command command, String alias, String[] args);
}
