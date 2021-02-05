package org.betonquest.betonquest.conditions;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.entity.Player;

/**
 * Checks if a player is facing a direction
 * <p>
 * Created on 01.10.2018.
 */
@SuppressWarnings("PMD.CommentRequired")
public class FacingCondition extends Condition {

    private final Direction direction;

    public FacingCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        direction = instruction.getEnum(Direction.class);
    }

    @Override
    @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
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
