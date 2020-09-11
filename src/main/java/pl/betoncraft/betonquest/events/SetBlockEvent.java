package pl.betoncraft.betonquest.events;

import org.bukkit.Location;
import org.bukkit.Material;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.LocationData;

/**
 * Sets the block at specified location
 */
public class SetBlockEvent extends QuestEvent {

    private final Material block;
    private final LocationData loc;

    public SetBlockEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        staticness = true;
        persistent = true;
        block = instruction.getMaterial(instruction.next());
        loc = instruction.getLocation();
    }

    @Override
    protected Void execute(final String playerID) throws QuestRuntimeException {
        final Location location = loc.getLocation(playerID);
        location.getBlock().setType(block);
        return null;
    }

}
