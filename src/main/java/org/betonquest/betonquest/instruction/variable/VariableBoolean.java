package org.betonquest.betonquest.instruction.variable;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.kernel.processor.quest.VariableProcessor;

/**
 * Represents a boolean that can contain variables.
 */
public class VariableBoolean extends Variable<Boolean> {
    /**
     * The string representation of a boolean true.
     */
    private static final String TRUE = "true";

    /**
     * The string representation of a boolean false.
     */
    private static final String FALSE = "false";

    /**
     * Resolves a string that may contain variables to a variable of the given type.
     *
     * @param variableProcessor the processor to create the variables
     * @param pack              the package in which the variable is used in
     * @param input             the string that may contain variables
     * @throws QuestException if the variables could not be created or resolved to the given type
     */
    public VariableBoolean(final VariableProcessor variableProcessor, final QuestPackage pack, final String input) throws QuestException {
        super(variableProcessor, pack, input, value -> {
            if (TRUE.equalsIgnoreCase(value)) {
                return true;
            } else if (FALSE.equalsIgnoreCase(value)) {
                return false;
            } else {
                throw new QuestException("Could not parse value to boolean: " + value);
            }
        });
    }
}
