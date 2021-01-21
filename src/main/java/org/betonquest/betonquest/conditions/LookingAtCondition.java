package org.betonquest.betonquest.conditions;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.BlockSelector;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.betonquest.betonquest.utils.location.CompoundLocation;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 * Checks if a player is looking at a specific block
 * <p>
 * Created on 01.10.2018.
 */
@SuppressWarnings("PMD.CommentRequired")
public class LookingAtCondition extends Condition {

    private final CompoundLocation loc;
    private final BlockSelector selector;
    private final boolean exactMatch;

    public LookingAtCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        loc = instruction.getLocation(instruction.getOptional("loc"));
        selector = instruction.getBlockSelector(instruction.getOptional("type"));
        exactMatch = instruction.hasArgument("exactMatch");
        if (loc == null && selector == null) {
            throw new InstructionParseException("You must define either 'loc:' or 'type:' optional");
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
            return selector.match(lookingAt, exactMatch);
        }
        return true;
    }

}
