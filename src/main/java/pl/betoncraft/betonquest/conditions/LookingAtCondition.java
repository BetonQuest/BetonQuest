/*
 * BetonQuest - advanced quests for Bukkit
 * Copyright (C) 2016  Jakub "Co0sh" Sapalski
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

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.BlockSelector;
import pl.betoncraft.betonquest.utils.LocationData;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Checks if a player is looking at a specific block
 * <p>
 * Created on 01.10.2018.
 *
 * @author Jonas Blocher
 */
public class LookingAtCondition extends Condition {

    private final LocationData loc;
    private final BlockSelector selector;

    public LookingAtCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        loc = instruction.getLocation(instruction.getOptional("loc"));
        selector = instruction.getBlockSelector(instruction.getOptional("type"));
        if (loc == null && selector == null) {
            throw new InstructionParseException("You must define either 'loc:' or 'type:' optional");
        }

        if (selector != null && !selector.isValid()) {
            throw new InstructionParseException("Invalid selector: " + selector.toString());
        }
    }

    @Override
    protected Boolean execute(final String playerID) throws QuestRuntimeException {
        final Player player = PlayerConverter.getPlayer(playerID);
        final Block lookingAt = player.getTargetBlock(null, 6);
        if (loc != null) {
            final Location targetLocation = loc.getLocation(playerID);
            final Location actualLocation = lookingAt.getLocation();
            if (targetLocation.getBlockX() != actualLocation.getBlockX()
                    || targetLocation.getBlockY() != actualLocation.getBlockY()
                    || targetLocation.getBlockZ() != actualLocation.getBlockZ()) {
                return false;
            }
        }
        if (selector != null) {
            return selector.match(lookingAt);
        }
        return true;
    }

}
