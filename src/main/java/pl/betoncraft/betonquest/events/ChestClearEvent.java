package pl.betoncraft.betonquest.events;

import org.bukkit.block.Block;
import org.bukkit.inventory.InventoryHolder;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.location.CompoundLocation;

/**
 * Clears a specified chest from all items inside.
 */
public class ChestClearEvent extends QuestEvent {

    private final CompoundLocation loc;

    public ChestClearEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        staticness = true;
        persistent = true;
        loc = instruction.getLocation();
    }

    @Override
    protected Void execute(final String playerID) throws QuestRuntimeException {
        final Block block = loc.getLocation(playerID).getBlock();
        final InventoryHolder chest;
        try {
            chest = (InventoryHolder) block.getState();
        } catch (ClassCastException e) {
            throw new QuestRuntimeException("Trying to clears items in a chest, but there's no chest! Location: X"
                    + block.getX() + " Y" + block.getY() + " Z" + block.getZ(), e);
        }
        chest.getInventory().clear();
        return null;
    }

}
