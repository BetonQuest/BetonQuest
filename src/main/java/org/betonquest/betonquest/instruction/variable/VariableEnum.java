package org.betonquest.betonquest.instruction.variable;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.quest.registry.processor.VariableProcessor;

import java.util.Locale;

/**
 * Represents an enum that can contain variables.
 *
 * @param <T> the type of the enum
 */
public class VariableEnum<T extends Enum<T>> extends Variable<T> {
    /**
     * Resolves a string that may contain variables to a variable of the given type.
     *
     * @param variableProcessor the processor to create the variables
     * @param pack              the package in which the variable is used in
     * @param input             the string that may contain variables
     * @param enumType          the type of the enum
     * @throws QuestException if the variables could not be created or resolved to the given type
     */
    public VariableEnum(final VariableProcessor variableProcessor, final QuestPackage pack, final String input, final Class<T> enumType) throws QuestException {
        super(variableProcessor, pack, input, value -> {
            return Enum.valueOf(enumType, value.toUpperCase(Locale.ROOT));
        });
    }
}
