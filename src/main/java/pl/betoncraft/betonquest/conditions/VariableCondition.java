package pl.betoncraft.betonquest.conditions;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;

/**
 * Checks if the variable value matches given pattern.
 */
public class VariableCondition extends Condition {

    private String variable;
    private String regex;

    public VariableCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, false);
        variable = instruction.next();
        regex = instruction.next().replace('_', ' ');
    }

    @Override
    protected Boolean execute(final String playerID) {
        return BetonQuest.getInstance().getVariableValue(instruction.getPackage().getName(), variable, playerID).matches(regex);
    }

}
