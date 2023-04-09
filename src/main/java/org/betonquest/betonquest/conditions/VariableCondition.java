package org.betonquest.betonquest.conditions;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableString;
import org.betonquest.betonquest.api.BetonQuestLogger;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;

import java.util.regex.PatternSyntaxException;

/**
 * Checks if the variable value matches given pattern.
 */
public class VariableCondition extends Condition {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private static final BetonQuestLogger LOG = BetonQuestLogger.create(VariableCondition.class);

    /**
     * The variable to compare with the regex.
     */
    private final VariableString variable;
    /**
     * The regex the variable must match.
     */
    private final VariableString regex;

    /**
     * Creates a new VariableCondition based on the given instruction.
     *
     * @param instruction the instruction to parse
     * @throws InstructionParseException if the instruction is invalid
     */
    public VariableCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, instruction.hasArgument("forceSync"));
        variable = new VariableString(instruction.getPackage(), instruction.next());
        regex = new VariableString(instruction.getPackage(), instruction.next(), true);
    }

    @Override
    protected Boolean execute(final Profile profile) {
        final String resolvedVariable = variable.getString(profile);
        final String resolvedRegex = regex.getString(profile);
        try {
            return resolvedVariable.matches(resolvedRegex);
        } catch (final PatternSyntaxException e) {
            final String variableAddress = this.instruction.getID().toString();
            LOG.warn("Invalid regular expression '%s' used in variable condition '%s'. Error: %s"
                    .formatted(e.getPattern(), variableAddress, e.getMessage()), e);
            return false;
        }
    }
}
