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

import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Checks if a player is facing a direction
 * <p>
 * Created on 01.10.2018.
 *
 * @author Jonas Blocher
 */
public class FacingCondition extends Condition {

    private final Direction direction;

    public FacingCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        direction = instruction.getEnum(Direction.class);
    }

    @Override
    protected Boolean execute(final String playerID) throws QuestRuntimeException {
        final Player player = PlayerConverter.getPlayer(playerID);
        float rotation = player.getLocation().getYaw();
        final float pitch = player.getLocation().getPitch();
        final Direction facing;
        if (pitch > 60) {
            facing = Direction.DOWN;
        } else if (pitch < -60) {
            facing = Direction.UP;
        } else {
            if (rotation < 0) {
                rotation += 360;
            }
            if (rotation < 45) {
                facing = Direction.SOUTH;
            } else if (rotation < 135) {
                facing = Direction.WEST;
            } else if (rotation < 225) {
                facing = Direction.NORTH;
            } else if (rotation < 325) {
                facing = Direction.EAST;
            } else {
                facing = Direction.SOUTH;
            }
        }
        return facing == direction;
    }

    private enum Direction {
        NORTH,
        EAST,
        SOUTH,
        WEST,
        UP,
        DOWN
    }
}
