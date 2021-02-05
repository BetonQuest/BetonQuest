package org.betonquest.betonquest.events;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.location.CompoundLocation;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Powerable;

import java.util.Locale;

/**
 * This event turns on, of or toggles levers.
 */
@SuppressWarnings("PMD.CommentRequired")
public class LeverEvent extends QuestEvent {

    private final CompoundLocation loc;
    private final ToggleType type;

    public LeverEvent(final Instruction instruction) throws InstructionParseException {
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
        if (!block.getType().equals(Material.LEVER)) {
            return null;
        }

        final Powerable lever = (Powerable) block.getBlockData();

        switch (type) {
            case ON:
                lever.setPowered(true);
                break;
            case OFF:
                lever.setPowered(false);
                break;
            case TOGGLE:
                lever.setPowered(!lever.isPowered());
                break;
        }
        block.setBlockData(lever);
        return null;
    }

    private enum ToggleType {
        ON, OFF, TOGGLE
    }

}
