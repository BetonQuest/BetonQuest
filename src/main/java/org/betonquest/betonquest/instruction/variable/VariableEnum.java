package org.betonquest.betonquest.instruction.variable;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.kernel.processor.quest.VariableProcessor;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

/**
 * Represents an enum that can contain variables.
 *
 * @param <T> the type of the enum
 */
public class VariableEnum<T extends Enum<T>> extends Variable<T> {
    /**
     * Resolves a string that may contain variables to an enum.
     * <p>
     * The enum needs to be in upper case.
     *
     * @param variableProcessor the processor to create the variables
     * @param pack              the package in which the variable is used in
     * @param input             the string that may contain variables
     * @param enumType          the type of the enum
     * @throws QuestException if the variables could not be created or resolved to the given type
     */
    public VariableEnum(final VariableProcessor variableProcessor, @Nullable final QuestPackage pack, final String input, final Class<T> enumType) throws QuestException {
        super(variableProcessor, pack, input, value -> {
            final String upperValue = value.toUpperCase(Locale.ROOT);
            try {
                return Enum.valueOf(enumType, upperValue);
            } catch (final IllegalArgumentException e) {
                throw new QuestException("Invalid enum value: " + upperValue, e);
            }
        });
    }
}
