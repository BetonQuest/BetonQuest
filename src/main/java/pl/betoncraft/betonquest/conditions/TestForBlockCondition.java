package pl.betoncraft.betonquest.conditions;

import org.bukkit.block.Block;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.BlockSelector;
import pl.betoncraft.betonquest.utils.LocationData;

/**
 * Checks block at specified location against specified Material
 */
public class TestForBlockCondition extends Condition {

    private final LocationData loc;
    private final BlockSelector selector;
    private final boolean exactMatch;

    public TestForBlockCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        staticness = true;
        persistent = true;
        loc = instruction.getLocation();
        selector = instruction.getBlockSelector();
        exactMatch = instruction.hasArgument("exactMatch");
    }

    @Override
    protected Boolean execute(final String playerID) throws QuestRuntimeException {
        final Block block = loc.getLocation(playerID).getBlock();

        return selector.match(block, exactMatch);
    }

}
