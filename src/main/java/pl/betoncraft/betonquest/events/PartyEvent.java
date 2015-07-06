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
import pl.betoncraft.betonquest.core.InstructionParseException;
import pl.betoncraft.betonquest.utils.Utils;

/**
 * Fires specified events for every player in the party
 * 
 * @author Jakub Sapalski
 */
public class PartyEvent extends QuestEvent {
    
    private final String[] conditions;
    private final String[] events;
    private final double   range;

    public PartyEvent(String packName, String instructions)
            throws InstructionParseException {
        super(packName, instructions);
        String[] parts = instructions.split(" ");
        if (parts.length < 4) {
            throw new InstructionParseException("Not enough arguments");
        }
        // load conditions and events
        String[] tempConditions = parts[2].split(",");
        for (int i = 0; i < tempConditions.length; i++) {
            if (!tempConditions[i].contains(".")) {
                tempConditions[i] = packName + "." + tempConditions[i];
            }
        }
        conditions = tempConditions;
        String[] tempEvents = parts[3].split(",");
        for (int i = 0; i < tempEvents.length; i++) {
            if (!tempEvents[i].contains(".")) {
                tempEvents[i] = packName + "." + tempEvents[i];
            }
        }
        events = tempEvents;
        // load the range
        try {
            range = Double.parseDouble(parts[1]);
            if (range <= 0) {
                throw new InstructionParseException("Range must be positive");
            }
        } catch (NumberFormatException e) {
            throw new InstructionParseException("Cannot parse range");
        }
    }

    @Override
    public void run(String playerID) {
        ArrayList<String> members = Utils.getParty(playerID, range,
                pack.getName(), conditions);
        for (String memberID : members) {
            for (String event : events) {
                BetonQuest.event(memberID, event);
            }
        }
    }

}
