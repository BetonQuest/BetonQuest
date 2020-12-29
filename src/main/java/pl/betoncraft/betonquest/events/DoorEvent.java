package pl.betoncraft.betonquest.events;

import org.bukkit.block.Block;
import org.bukkit.block.data.Openable;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.location.CompoundLocation;

import java.util.Locale;

/**
 * This event opens/closes/toggles doors, trapdoors and gates.
 */
@SuppressWarnings({"deprecation", "PMD.CommentRequired"})
public class DoorEvent extends QuestEvent {

    private final CompoundLocation loc;
    private final ToggleType type;

    public DoorEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        staticness = true;
        persistent = true;
        loc = instruction.getLocation();
        final String action = instruction.next();
        try {
            type = ToggleType.valueOf(action.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new InstructionParseException("Unknown action type '" + action + "', allowed are: on, off, toggle", e);
        }
    }

    @Override
    protected Void execute(final String playerID) throws QuestRuntimeException {
        final Block block = loc.getLocation(playerID).getBlock();

        final Openable door = (Openable) block.getBlockData();

        switch (type) {
            case ON:
                door.setOpen(true);
                break;
            case OFF:
                door.setOpen(false);
                break;
            case TOGGLE:
                door.setOpen(!door.isOpen());
                break;
        }
        block.setBlockData(door);
        return null;
    }

    private enum ToggleType {
        ON, OFF, TOGGLE
    }

}
