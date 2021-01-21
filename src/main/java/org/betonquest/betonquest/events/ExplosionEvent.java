package org.betonquest.betonquest.events;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.location.CompoundLocation;
import org.bukkit.Location;

/**
 * Spawns an explosion in a given location and with given flags
 */
@SuppressWarnings("PMD.CommentRequired")
public class ExplosionEvent extends QuestEvent {

    private final boolean setsFire;
    private final boolean breaksBlocks;
    private final VariableNumber power;
    private final CompoundLocation loc;

    public ExplosionEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        staticness = true;
        persistent = true;
        setsFire = "1".equals(instruction.next());
        breaksBlocks = "1".equals(instruction.next());
        power = instruction.getVarNum();
        loc = instruction.getLocation();

    }

    @Override
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    protected Void execute(final String playerID) throws QuestRuntimeException {
        final Location location = loc.getLocation(playerID);
        location.getWorld().createExplosion(location.getX(), location.getY(), location.getZ(),
                (float) power.getDouble(playerID), setsFire, breaksBlocks);
        return null;
    }
}
