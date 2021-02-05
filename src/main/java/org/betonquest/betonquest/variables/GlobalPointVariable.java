package org.betonquest.betonquest.variables;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.exceptions.InstructionParseException;

/**
 * Allows you to display total amount of global points or amount of global points remaining to
 * some other amount.
 */
@SuppressWarnings("PMD.CommentRequired")
public class GlobalPointVariable extends PointVariable {

    public GlobalPointVariable(final Instruction instruction) throws InstructionParseException {
        super(instruction);
    }

    @Override
    public String getValue(final String playerID) {
        return getValue(BetonQuest.getInstance().getGlobalData().getPoints());
    }

}
