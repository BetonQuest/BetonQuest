package org.betonquest.betonquest.events;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.location.CompoundLocation;
import org.bukkit.block.Block;
import org.bukkit.inventory.InventoryHolder;

/**
 * Clears a specified chest from all items inside.
 */
@SuppressWarnings({"PMD.CommentRequired", "PMD.CommentRequired"})
public class ChestClearEvent extends QuestEvent {

    private final CompoundLocation loc;

    public ChestClearEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        staticness = true;
        persistent = true;
        loc = instruction.getLocation();
    }

    @Override
    protected Void execute(final Profile profile) throws QuestRuntimeException {
        final Block block = loc.getLocation(profile).getBlock();
        final InventoryHolder chest;
        try {
            chest = (InventoryHolder) block.getState();
        } catch (final ClassCastException e) {
            throw new QuestRuntimeException("Trying to clears items in a chest, but there's no chest! Location: X"
                    + block.getX() + " Y" + block.getY() + " Z" + block.getZ(), e);
        }
        chest.getInventory().clear();
        return null;
    }

}
