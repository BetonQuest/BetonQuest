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
public class DeleteObjectiveEvent extends QuestEvent {

    /**
     * Constructor method
     * 
     * @param playerID
     * @param instructions
     */
    public DeleteObjectiveEvent(String playerID, String packName, String instructions) {
        super(playerID, packName, instructions);
        // check if playerID isn't null, this event cannot be static
        if (playerID == null) {
            Debug.error("This event cannot be static: " + instructions);
            return;
        }
        final String tag = instructions.split(" ")[1];
        if (PlayerConverter.getPlayer(playerID) != null) {
            BetonQuest.getInstance().getDBHandler(playerID).deleteObjective(tag);
        } else {
            if (BetonQuest.getInstance().isMySQLUsed()) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        deleteOfflineObjective(tag);
                    }
                }.runTaskAsynchronously(BetonQuest.getInstance());
            } else {
                deleteOfflineObjective(tag);
            }
        }
    }
    
    private void deleteOfflineObjective(String tag) {
        DatabaseHandler dbHandler = new DatabaseHandler(playerID);
        dbHandler.deleteObjective(tag);
        dbHandler.saveData();
    }
}
