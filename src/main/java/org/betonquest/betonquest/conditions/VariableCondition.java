package org.betonquest.betonquest.conditions;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableString;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;

/**
 * Checks if the variable value matches given pattern.
 */
@SuppressWarnings("PMD.CommentRequired")
public class VariableCondition extends Condition {

    private final VariableString variable;
    private final VariableString regex;

    public VariableCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, instruction.hasArgument("forceSync"));
        variable = new VariableString(instruction.getPackage(), instruction.next());
        regex = new VariableString(instruction.getPackage(), instruction.next().replace('_', ' '));
    }

    @Override
    protected Boolean execute(final Profile profile) {
        final String resolvedVariable = variable.getString(profile);
        final String resolvedRegex = regex.getString(profile);
        return resolvedVariable.matches(resolvedRegex);
    }
}
