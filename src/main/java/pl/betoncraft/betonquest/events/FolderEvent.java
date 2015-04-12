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

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.scheduler.BukkitRunnable;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.utils.Debug;

/**
 * Folder event is a collection of other events, that can be run after
 * a delay and the events can be randomly choosen to run or not
 * 
 * @author Coosh
 */
public class FolderEvent extends QuestEvent {

    public FolderEvent(String playerID, String instructions) {
        super(playerID, instructions);
        // declare variables used later
        String[] parts = instructions.split(" ");
        String[] events = null;
        int delay = 0;
        int random = 0;
        // parse the instuction string
        for (String part : parts) {
            if (part.contains("events:")) {
                events = part.substring(7).split(",");
            }
            if (part.contains("delay:")) {
                try {
                    delay = Integer.parseInt(part.substring(6));
                } catch (NumberFormatException e) {
                    // if the delay is incorrect, there is an error
                    Debug.error("Wrong number format in folder event! " + instructions);
                    return;
                }
            }
            if (part.contains("random:")) {
                try {
                    random = Integer.parseInt(part.substring(7));
                } catch (NumberFormatException e) {
                    // if the random number is incorrect, there is an error
                    Debug.error("Wrong number format in folder event! " + instructions);
                    return;
                }
            }
        }
        // if there are no events, there is an error
        if (events == null) {
            BetonQuest.getInstance().getLogger()
                    .severe("Error in folder event: events not defined! " + instructions);
            return;
        }
        // choose randomly which events should be fired
        if (random > 0 && random <= events.length) {
            // copy events into the modifyable ArrayList
            ArrayList<String> eventsList = new ArrayList<>();
            for (String event : events) {
                eventsList.add(event);
            }
            // remove choosen events from that ArrayList and place them in a new list
            ArrayList<String> chosenList = new ArrayList<>();
            for (int i = random; i > 0; i--) {
                int chosen = new Random().nextInt(eventsList.size());
                chosenList.add(eventsList.remove(chosen));
            }
            // convert that new list into the array and replace "events" with it
            events = new String[chosenList.size()];
            events = chosenList.toArray(events);
        }
        // execute events after the delay
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
        }.runTaskLater(BetonQuest.getInstance(), delay * 20); // 20 ticks is a second
    }

}
