/**
 * BetonQuest - advanced quests for Bukkit
 * Copyright (C) 2016  Jakub "Co0sh" Sapalski
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package pl.betoncraft.betonquest.conditions;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.QuestRuntimeException;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.utils.LocationData;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Checks if a player is looking at a specific block
 *
 * Created on 01.10.2018.
 *
 * @author Jonas Blocher
 */
public class LookingAtCondition extends Condition {

    private final LocationData loc;
    private final Material type;

    public LookingAtCondition(Instruction instruction) throws InstructionParseException {
        super(instruction);
        loc = instruction.getLocation(instruction.getOptional("loc"));
        type = instruction.getMaterial(instruction.getOptional("type"));
        if (loc == null && type == null) throw new InstructionParseException("You must define either 'loc:' or 'type:' optional");
    }

    @Override
    public boolean check(String playerID) throws QuestRuntimeException {
        Player p = PlayerConverter.getPlayer(playerID);
        Block lookingAt = p.getTargetBlock(null, 6);
        if (loc != null) {
            Location location = loc.getLocation(playerID);
            Location to = lookingAt.getLocation();
            if (location.getBlockX() != to.getBlockX()
                    || location.getBlockY() != to.getBlockY()
                    || location.getBlockZ() != to.getBlockZ()) return false;
        }
        if (type != null) {
            if (type != lookingAt.getType()) return false;
        }
        return true;
    }

}
