package org.betonquest.betonquest.instruction.variable;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.quest.registry.processor.VariableProcessor;

/**
 * Represents a boolean that can contain variables.
 */
public class VariableBoolean extends Variable<Boolean> {
    /**
     * Resolves a string that may contain variables to a variable of the given type.
     *
     * @param variableProcessor the processor to create the variables
     * @param pack              the package in which the variable is used in
     * @param input             the string that may contain variables
     * @throws InstructionParseException if the variables could not be created or resolved to the given type
     */
    @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
    public VariableBoolean(final VariableProcessor variableProcessor, final QuestPackage pack, final String input) throws InstructionParseException {
        super(variableProcessor, pack, input, value -> {
            if ("true".equalsIgnoreCase(value)) {
                return true;
            } else if ("false".equalsIgnoreCase(value)) {
                return false;
            } else {
                throw new QuestRuntimeException("Could not parse value to boolean: " + value);
            }
        });
    }
}
