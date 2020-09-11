package pl.betoncraft.betonquest.variables;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;

/**
 * Allows you to display total amount of global points or amount of global points remaining to
 * some other amount.
 */
public class GlobalPointVariable extends PointVariable {

    protected String category;
    protected Type type;
    protected int amount;

    public GlobalPointVariable(final Instruction instruction) throws InstructionParseException {
        super(instruction);
    }

    @Override
    public String getValue(final String playerID) {
        return getValue(BetonQuest.getInstance().getGlobalData().getPoints());
    }

}
