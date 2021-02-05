package org.betonquest.betonquest.events;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.BlockSelector;
import org.betonquest.betonquest.utils.location.CompoundLocation;
import org.bukkit.Location;

/**
 * Sets a block specified as {@link BlockSelector} at specified location
 */
@SuppressWarnings("PMD.CommentRequired")
public class SetBlockEvent extends QuestEvent {

    private final BlockSelector selector;
    private final CompoundLocation loc;
    private final boolean applyPhysics;

    public SetBlockEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        staticness = true;
        persistent = true;
        selector = instruction.getBlockSelector(instruction.next());
        applyPhysics = !instruction.hasArgument("ignorePhysics");
        loc = instruction.getLocation();
    }

    @Override
    protected Void execute(final String playerID) throws QuestRuntimeException {
        final Location location = loc.getLocation(playerID);
        selector.setToBlock(location.getBlock(), applyPhysics);
        return null;
    }

}
