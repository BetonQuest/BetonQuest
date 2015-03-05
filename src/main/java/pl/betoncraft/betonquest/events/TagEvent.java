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
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * 
 * @author Co0sh
 */
public class TagEvent extends QuestEvent {

    /**
     * Constructor method
     * 
     * @param playerID
     * @param instructions
     */
    public TagEvent(String playerID, String instructions) {
        super(playerID, instructions);
        final String[] parts = instructions.split(" ");
        if (PlayerConverter.getPlayer(playerID) != null) {
            switch (parts[1]) {
                case "add":
                    for (String tag : parts[2].split(",")) {
                        BetonQuest.getInstance().getDBHandler(playerID).addTag(tag);
                    }
                    break;
                default:
                    for (String tag : parts[2].split(",")) {
                        BetonQuest.getInstance().getDBHandler(playerID).removeTag(tag);
                    }
                    break;
            }
        } else {
            if (BetonQuest.getInstance().isMySQLUsed()) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        addOfflineTag(parts);
                    }
                }.runTaskAsynchronously(BetonQuest.getInstance());
            } else {
                addOfflineTag(parts);
            }
        }
        
    }
    
    private void addOfflineTag(String[] parts) {
        DatabaseHandler dbHandler = new DatabaseHandler(playerID);
        switch (parts[1]) {
            case "add":
                for (String tag : parts[2].split(",")) {
                    dbHandler.addTag(tag);
                }
                break;
            default:
                for (String tag : parts[2].split(",")) {
                    dbHandler.removeTag(tag);
                }
                break;
        }
        dbHandler.saveData();
    }
}
