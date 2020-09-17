package pl.betoncraft.betonquest.events;

import org.bukkit.Location;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.BlockSelector;
import pl.betoncraft.betonquest.utils.LocationData;

/**
 * Sets a block specified as {@link BlockSelector} at specified location
 */
public class SetBlockEvent extends QuestEvent {

    private final BlockSelector selector;
    private final LocationData loc;
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
