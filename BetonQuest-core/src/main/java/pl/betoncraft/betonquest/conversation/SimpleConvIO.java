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
package pl.betoncraft.betonquest.conversation;

import org.bukkit.ChatColor;

/**
 * Simple chat-based conversation output.
 *
 * @author Jakub Sapalski
 */
public class SimpleConvIO extends ChatConvIO {

    private String optionFormat;

    public SimpleConvIO(Conversation conv, String playerID) {
        super(conv, playerID);
        StringBuilder string = new StringBuilder();
        for (ChatColor color : colors.get("number")) {
            string.append(color);
        }
        string.append("%number%. ");
        for (ChatColor color : colors.get("option")) {
            string.append(color);
        }
        optionFormat = string.toString();
    }

    @Override
    public void display() {
        super.display();
        for (int i = 1; i <= options.size(); i++) {
            conv.sendMessage(optionFormat.replace("%number%", Integer.toString(i)) + options.get(i));
        }
    }
}
