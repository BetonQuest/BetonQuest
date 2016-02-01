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
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.Point;
import pl.betoncraft.betonquest.VariableNumber;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.database.DatabaseHandler;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Modified player's points
 * 
 * @author Jakub Sapalski
 */
public class PointEvent extends QuestEvent {
    
    final VariableNumber count;
    final boolean multi;
    final String category; 

    public PointEvent(String packName, String instructions)
            throws InstructionParseException {
        super(packName, instructions);
        persistent = true;
        String[] parts = instructions.split(" ");
        if (parts.length < 3) {
            throw new InstructionParseException("Not enough arguments");
        }
        category = parts[1].contains(".") ? parts[1] : packName + "." + parts[1];
        if (parts[2].startsWith("*")) {
            multi = true;
            parts[2] = parts[2].replace("*", "");
        } else {
            multi = false;
        }
        try {
            count = new VariableNumber(packName, parts[2]);
        } catch (NumberFormatException e) {
            throw new InstructionParseException("Could not parse point count");
        }
    }

    @Override
    public void run(final String playerID) {
        if (PlayerConverter.getPlayer(playerID) == null) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    DatabaseHandler dbHandler = new DatabaseHandler(playerID);
                    addPoints(playerID, dbHandler);
                }
            }.runTaskAsynchronously(BetonQuest.getInstance());
        } else {
            DatabaseHandler dbHandler = BetonQuest.getInstance().getDBHandler(playerID);
            addPoints(playerID, dbHandler);
        }
    }

    private void addPoints(String playerID, DatabaseHandler dbHandler) {
        if (multi) {
            for (Point p : dbHandler.getPoints()) {
                if (p.getCategory().equalsIgnoreCase(category)) {
                    dbHandler.addPoints(category, (int) Math.floor((p.getCount() * count.getDouble(playerID)) - p.getCount()));
                }
            }
        } else {
            dbHandler.addPoints(category, (int) Math.floor(count.getDouble(playerID)));
        }
    }
}
