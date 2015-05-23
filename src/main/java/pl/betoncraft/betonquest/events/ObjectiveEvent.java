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
import pl.betoncraft.betonquest.database.DatabaseHandler;
import pl.betoncraft.betonquest.utils.Debug;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * 
 * @author Co0sh
 */
public class ObjectiveEvent extends QuestEvent {

    /**
     * Constructor method
     * 
     * @param playerID
     * @param instructions
     */
    public ObjectiveEvent(String playerID, String packName, String instructions) {
        super(playerID, packName, instructions);
        // check if playerID isn't null, this event cannot be static
        if (playerID == null) {
            Debug.error("This event cannot be static: " + instructions);
            return;
        }
        int index = instructions.trim().indexOf(" ") + 1;
        if (index == 0) {
            Debug.error("Objective not defined in event: " + instructions);
            return;
        }
        String objective = instructions.substring(index);
        String[] parts = objective.split(" ");
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
        final String finalObjective = builder.toString().trim();
        if (PlayerConverter.getPlayer(playerID) == null) {
            if (BetonQuest.getInstance().isMySQLUsed()) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        addOfflineObjective(finalObjective);
                    }
                }.runTaskAsynchronously(BetonQuest.getInstance());
            } else {
                addOfflineObjective(finalObjective);
            }
        } else {
            BetonQuest.objective(playerID, finalObjective);
        }
    }

    private void addOfflineObjective(String objective) {
        DatabaseHandler dbHandler = new DatabaseHandler(playerID);
        dbHandler.addRawObjective(objective);
        dbHandler.saveData();
    }
}
