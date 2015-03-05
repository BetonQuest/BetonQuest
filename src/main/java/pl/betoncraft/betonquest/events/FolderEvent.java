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
import pl.betoncraft.betonquest.utils.Debug;

public class FolderEvent extends QuestEvent {

    public FolderEvent(String playerID, String instructions) {
        super(playerID, instructions);
        String[] parts = instructions.split(" ");
        String[] events = null;
        int delay = 0;
        for (String part : parts) {
            if (part.contains("events:")) {
                events = part.substring(7).split(",");
            }
            if (part.contains("delay:")) {
                delay = Integer.parseInt(part.substring(6));
            }
        }
        if (events == null) {
            BetonQuest.getInstance().getLogger()
                    .severe("Error in folder event: events not defined! " + instructions);
            return;
        }
        final String[] finalEvents = events;
        final String player = playerID;
        new BukkitRunnable() {
            @Override
            public void run() {
                Debug.info("Running folder events for player " + player);
                for (String event : finalEvents) {
                    BetonQuest.event(player, event);
                }
            }
        }.runTaskLater(BetonQuest.getInstance(), delay * 20);
    }

}
