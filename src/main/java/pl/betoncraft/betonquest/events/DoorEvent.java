package pl.betoncraft.betonquest.events;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Openable;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.LocationData;

import java.util.Locale;

/**
 * This event opens/closes/toggles doors, trapdoors and gates.
 */
@SuppressWarnings("deprecation")
public class DoorEvent extends QuestEvent {

    private LocationData loc;
    private ToggleType type;

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
        final BlockState state = block.getState();
        final MaterialData data = state.getData();
        if (data instanceof Openable) {
            final Openable openable = (Openable) data;
            switch (type) {
                case ON:
                    openable.setOpen(true);
                    break;
                case OFF:
                    openable.setOpen(false);
                    break;
                case TOGGLE:
                    openable.setOpen(!openable.isOpen());
                    break;
            }
            state.setData((MaterialData) openable);
            state.update();
        }
        return null;
    }

    private enum ToggleType {
        ON, OFF, TOGGLE
    }

}
