package org.betonquest.betonquest.events;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.location.CompoundLocation;
import org.bukkit.block.Block;
import org.bukkit.block.data.Openable;

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
