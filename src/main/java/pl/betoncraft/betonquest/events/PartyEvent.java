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

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.utils.Debug;
import pl.betoncraft.betonquest.utils.Utils;

/**
 * Fires specified events for every player in the party
 * 
 * @author Jakub Sapalski
 */
public class PartyEvent extends QuestEvent {
    
    private String[] conditions;
    private String[] events;
    private double   range;

    public PartyEvent(String playerID, String pack, String instructions) {
        super(playerID, pack, instructions);
        String[] parts = instructions.split(" ");
        if (parts.length < 4) {
            Debug.error("Not enough arguments in party event: " + instructions);
            return;
        }
        // load conditions and events
        conditions = parts[2].split(",");
        events     = parts[3].split(",");
        // load the range
        try {
            range = Double.parseDouble(parts[1]);
        } catch (NumberFormatException e) {
            Debug.error("Cannot parse range in party event: " + instructions);
            return;
        }
        // everything loaded
        
        // firing the event
        ArrayList<String> members = Utils.getParty(playerID, range, pack, conditions);
        for (String memberID : members) {
            for (String event : events) {
                String eventName;
                String packName;
                if (event.contains(".")) {
                    String[] eventParts = event.split("\\.");
                    eventName = eventParts[1];
                    packName = eventParts[0];
                } else {
                    eventName = event;
                    packName = super.packName;
                }
                BetonQuest.event(memberID, packName, eventName);
            }
        }
    }

}
