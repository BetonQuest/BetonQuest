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

import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Checks if the time is right
 * 
 * @author BYK
 */
public class TimeCondition extends Condition {

    private double timeMin = 0;
    private double timeMax = 0;

    public TimeCondition(String playerID, String instructions) {
        super(playerID, instructions);
        String theTime = null;
        String[] parts = instructions.split(" ");
        for (String part : parts) {
            if (part.contains("time:")) {
                theTime = part.substring(5);
            }
        }
        parts = theTime.split("-");
        timeMin = Double.parseDouble(parts[0]);
        timeMax = Double.parseDouble(parts[1]);
    }

    @Override
    public boolean isMet() {
        double time = PlayerConverter.getPlayer(playerID).getWorld().getTime();
        if (time >= 18000) {
            // 18000 minecraft-time is midnight, so there is new
            // normal-time cycle after that; subtracting 18 hours
            // from it makes sure that hour is correct in normal-time
            time = (time / 1000) - 18;
        } else {
            // if it's less than 18000, then normal-time is in current
            // minecraft-time cycle, but 6 hours behind, so add 6 hours
            time = (time / 1000) + 6;
        }
        if (time >= timeMin && time <= timeMax) {
            return true;
        }
        return false;
    }

}
