package pl.betoncraft.betonquest.conditions;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;

/**
 * Requires the specified global tag to be set
 */
public class GlobalTagCondition extends TagCondition {

    public GlobalTagCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        staticness = true;
        persistent = true;
    }

    @Override
    protected Boolean execute(final String playerID) {
        return BetonQuest.getInstance().getGlobalData().hasTag(tag);
    }

}
