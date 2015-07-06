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

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.core.InstructionParseException;

/**
 * Requires the player to have specified tag
 * 
 * @author Jakub Sapalski
 */
public class TagCondition extends Condition {

    private final String tag;

    public TagCondition(String packName, String instructions)
            throws InstructionParseException {
        super(packName, instructions);
        String[] parts = instructions.split(" ");
        if (parts.length < 2) {
            throw new InstructionParseException("There is no tag defined");
        }
        String tempTag = parts[1];
        String prefix = Config.getPackage(packName).getMain().getConfig()
                .getString("tag_point_prefix");
        if (prefix != null && prefix.equalsIgnoreCase("true") && !parts[1]
                .contains(".")) {
            tempTag = packName + "." + tempTag;
        }
        tag = tempTag;
    }

    @Override
    public boolean check(String playerID) {
        if (BetonQuest.getInstance().getDBHandler(playerID).hasTag(tag)) {
            return true;
        }
        return false;
    }

}
