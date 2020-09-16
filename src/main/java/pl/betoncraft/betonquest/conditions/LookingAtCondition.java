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
 */
public class LookingAtCondition extends Condition {

    private final LocationData loc;
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
