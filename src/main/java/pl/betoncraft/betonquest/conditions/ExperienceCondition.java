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
import pl.betoncraft.betonquest.utils.Debug;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Requires the player to have specified level of experience (or more)
 * 
 * @author Coosh
 */
public class ExperienceCondition extends Condition {

    private int experience;

    public ExperienceCondition(String playerID, String packName, String instructions) {
        super(playerID, packName, instructions);
        String[] parts = instructions.split(" ");
        if (parts.length < 2) {
            Debug.error("Experience levek not defined in experience condition: " + instructions);
            isOk = false;
            return;
        }
        try {
            experience = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            Debug.error("Could not parse experience level: " + instructions);
            isOk = false;
            return;
        }
    }

    @Override
    public boolean isMet() {
        if (!isOk) {
            Debug.error("There was an error, returning false.");
            return false;
        }
        if (PlayerConverter.getPlayer(playerID).getLevel() >= experience) {
            return true;
        }
        return false;
    }

}
