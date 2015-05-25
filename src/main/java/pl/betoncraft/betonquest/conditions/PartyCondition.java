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
package pl.betoncraft.betonquest.conditions;

import java.util.ArrayList;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.utils.Debug;
import pl.betoncraft.betonquest.utils.Utils;

/**
 * Checks the conditions for the whole party
 * (including the player that started the checking)
 * 
 * @author Jakub Sapalski
 */
public class PartyCondition extends Condition {
    
    private double   distance;
    private String[] conditions;
    private String[] everyone   = new String[]{};
    private String[] anyone     = new String[]{};
    private int      count      = 0;

    public PartyCondition(String playerID, String pack, String instructions) {
        super(playerID, pack, instructions);
        String[] parts = instructions.split(" ");
        if (parts.length < 4) {
            Debug.error("Not enough arguments in party condition: " + instructions);
            isOk = false;
            return;
        }
        // first argument is the distance
        try {
            distance = Double.parseDouble(parts[1]);
        } catch (NumberFormatException e) {
            Debug.error("Could not parse distance in party condition: " + instructions);
            isOk = false;
            return;
        }
        // next are conditions
        conditions = parts[2].split(",");
        // now time for everything else
        for (String part : parts) {
            if (part.startsWith("every:")) {
                everyone = part.substring(6).split(",");
            } else if (part.startsWith("any:")) {
                anyone = part.substring(4).split(",");
            } else if (part.startsWith("count:")) {
                try {
                    count = Integer.parseInt(part.substring(6));
                } catch (NumberFormatException e) {
                    Debug.error("Could not parse \"count\" argument in party condition: " + instructions);
                    isOk = false;
                    return;
                }
            }
        }
        // everything loaded
    }

    @Override
    public boolean isMet() {
        if (!isOk) {
            Debug.error("There was an error, returning false");
            return false;
        }
        // get the party
        ArrayList<String> members = Utils.getParty(playerID, distance, pack.getName(), conditions);
        // check every condition against every player - all of them must meet those conditions
        for (String condition : everyone) {
            String condName;
            String packName;
            if (condition.contains(".")) {
                String[] parts = condition.split("\\.");
                condName = parts[1];
                packName = parts[0];
            } else {
                condName = condition;
                packName = super.packName;
            }
            for (String memberID : members) {
                // if this condition wasn't met by someone, return false
                if (!BetonQuest.condition(memberID, packName, condName)) {
                    return false;
                }
            }
        }
        // check every condition against every player - at least one of them must meet each of those
        for (String condition : anyone) {
            boolean met = false;
            String condName;
            String packName;
            if (condition.contains(".")) {
                String[] parts = condition.split("\\.");
                condName = parts[1];
                packName = parts[0];
            } else {
                condName = condition;
                packName = super.packName;
            }
            for (String memberID : members) {
                if (BetonQuest.condition(memberID, packName, condName)) {
                    met = true;
                    break;
                }
            }
            // if this condition wasn't met by anyone, return false
            if (!met) {
                return false;
            }
        }
        // if the count is more than 0, we need to check if there are more players
        // in the party than required minimum
        if (count > 0 && members.size() < count) {
            return false;
        }
        // every check was passed, the party meets all conditions
        return true;
    }

}
