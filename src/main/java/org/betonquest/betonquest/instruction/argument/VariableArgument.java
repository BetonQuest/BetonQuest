package org.betonquest.betonquest.instruction.argument;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.variable.VariableEnum;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.instruction.variable.VariableString;
import org.betonquest.betonquest.kernel.processor.quest.VariableProcessor;

/**
 * Objectified parser for the Instruction.
 *
 * @param <T> what the argument returns
 */
@FunctionalInterface
public interface VariableArgument<T> {

    /**
     * {@link VariableNumber} argument with {@link VariableNumber#NOT_LESS_THAN_ZERO_CHECKER}.
     */
    VariableArgument<VariableNumber> NUMBER_NOT_LESS_THAN_ZERO = (variableProcessor, pack, input) ->
            new VariableNumber(variableProcessor, pack, input, VariableNumber.NOT_LESS_THAN_ZERO_CHECKER);

    /**
     * {@link VariableNumber} argument with {@link VariableNumber#NOT_LESS_THAN_ONE_CHECKER}.
     */
    VariableArgument<VariableNumber> NUMBER_NOT_LESS_THAN_ONE = (variableProcessor, pack, input) ->
            new VariableNumber(variableProcessor, pack, input, VariableNumber.NOT_LESS_THAN_ONE_CHECKER);

    /**
     * {@link VariableString} argument with the {@code replaceUnderscores} flag set.
     */
    VariableArgument<VariableString> STRING_REPLACE_UNDERSCORES = (variableProcessor, pack, input) ->
            new VariableString(variableProcessor, pack, input, true);

    /**
     * A variable interpreted as enum when resolved.
     *
     * @param enumClass the argument to parse the id
     * @param <T>       the ID type
     * @return the variables of the id
     */
    static <T extends Enum<T>> VariableArgument<VariableEnum<T>> ofEnum(final Class<T> enumClass) {
        return (variableProcessor, pack, string)
                -> new VariableEnum<>(variableProcessor, pack, string, enumClass);
    }

    /**
     * Gets a {@link T} from string.
     *
     * @param variableProcessor the variable processor for resolving
     * @param pack              the source package
     * @param string            the string to parse
     * @return the {@link T}
     * @throws QuestException when the string cannot be parsed as {@link T}
     */
    T convert(VariableProcessor variableProcessor, QuestPackage pack, String string) throws QuestException;
}
