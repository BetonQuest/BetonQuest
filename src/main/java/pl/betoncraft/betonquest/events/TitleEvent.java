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
package pl.betoncraft.betonquest.events;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.LogUtils;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class TitleEvent extends QuestEvent {

    protected Map<String, String> messages = new HashMap<>();
    protected List<String> variables = new ArrayList<>();
    protected int fadeIn, stay, fadeOut;

    public TitleEvent(Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        String times = instruction.next();
        if (!times.matches("^\\d+;\\d+;\\d+$")) {
            throw new InstructionParseException("Could not parse title time.");
        }
        String[] timeParts = times.split(";");
        try {
            fadeIn = Integer.parseInt(timeParts[0]);
            stay = Integer.parseInt(timeParts[1]);
            fadeOut = Integer.parseInt(timeParts[2]);
        } catch (NumberFormatException e) {
            throw new InstructionParseException("Could not parse title time.", e);
        }
        if(fadeIn == 0 && stay == 0 && fadeOut == 0) {
            fadeIn = 20;
            stay = 100;
            fadeOut = 20;
        }
        String[] parts = instruction.getInstruction().split(" ");
        String currentLang = Config.getLanguage();
        StringBuilder string = new StringBuilder();
        for (int i = 3; i < parts.length; i++) {
            String part = parts[i];
            if (part.startsWith("conditions:") || part.startsWith("condition:")) {
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
        for (String message : messages.values()) {
            for (String variable : BetonQuest.resolveVariables(message)) {
                try {
                    BetonQuest.createVariable(instruction.getPackage(), variable);
                } catch (InstructionParseException e) {
                    throw new InstructionParseException("Could not create '" + variable + "' variable: "
                            + e.getMessage(), e);
                }
                if (!variables.contains(variable))
                    variables.add(variable);
            }
        }
    }

    @Override
    protected Void execute(String playerID) throws QuestRuntimeException {
        String lang = BetonQuest.getInstance().getPlayerData(playerID).getLanguage();
        String message = messages.get(lang);
        if (message == null) {
            message = messages.get(Config.getLanguage());
        }
        if (message == null) {
            message = messages.values().iterator().next();
        }
        for (String variable : variables) {
            message = message.replace(variable,
                    BetonQuest.getInstance().getVariableValue(instruction.getPackage().getName(), variable, playerID));
        }
        message = ChatColor.translateAlternateColorCodes('&', message);
        String title = message;
        String subtitle = null;
        if(message.contains("|")){
            String[] parts = message.split("\\|");
            if(parts.length == 1) {
                if(message.startsWith("\\|")) {
                    title = null;
                    subtitle = parts[0];
                }
                else {
                    title = parts[0];
                    subtitle = null;
                }
            }
            else {
                title = parts[0];
                subtitle = parts[1];
            }
        }
        Player player = PlayerConverter.getPlayer(playerID);
        player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
        return null;
    }
}
