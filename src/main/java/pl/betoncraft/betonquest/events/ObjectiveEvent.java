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

import org.bukkit.scheduler.BukkitRunnable;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.core.InstructionParseException;
import pl.betoncraft.betonquest.database.DatabaseHandler;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Starts an objective for the player
 * 
 * @author Jakub Sapalski
 */
public class ObjectiveEvent extends QuestEvent {

    private final String objective;
    
    public ObjectiveEvent(String packName, String instructions)
            throws InstructionParseException {
        super(packName, instructions);
        persistent = true;
        String tempObjective;
        try {
            tempObjective = instructions.trim().substring(10);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new InstructionParseException(
                    "Objective instruction not defined");
        }
        String[] parts = tempObjective.split(" ");
        StringBuilder builder = new StringBuilder();
        for (String part : parts) {
            if (part.startsWith("events:")) {
                builder.append("events:");
                String[] events = part.substring(7).split(",");
                for (String event : events) {
                    if (!event.contains(".")) {
                        builder.append(packName + "." + event);
                    } else {
                        builder.append(event);
                    }
                    builder.append(",");
                }
                builder.deleteCharAt(builder.length() - 1);
            } else if (part.startsWith("conditions:")) {
                builder.append("conditions:");
                String[] conditions = part.substring(11).split(",");
                for (String condition : conditions) {
                    if (!condition.contains(".")) {
                        builder.append(packName + "." + condition);
                    } else {
                        builder.append(condition);
                    }
                    builder.append(",");
                }
                builder.deleteCharAt(builder.length() - 1);
            } else {
                builder.append(part);
            }
            builder.append(' ');
        }
        objective = builder.toString().trim();
        
    }

    @Override
    public void run(final String playerID) {
        if (PlayerConverter.getPlayer(playerID) == null) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    DatabaseHandler dbHandler = new DatabaseHandler(playerID);
                    dbHandler.addRawObjective(objective);
                    dbHandler.saveData();
                }
            }.runTaskAsynchronously(BetonQuest.getInstance());
        } else {
            BetonQuest.objective(playerID, objective);
        }
    }
}
