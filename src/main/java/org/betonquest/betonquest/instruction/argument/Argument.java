package org.betonquest.betonquest.instruction.argument;

import org.betonquest.betonquest.api.common.function.QuestFunction;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.betonquest.betonquest.instruction.variable.VariableList;

import java.util.List;

/**
 * Objectified parser for the Instruction to get a {@link T} from string.
 *
 * @param <T> what the argument returns
 */
@FunctionalInterface
public interface Argument<T> extends QuestFunction<String, T> {
    /**
     * Gets a list of {@link T}s from string.
     *
     * @param argument the argument to parse
     * @param <T>      what the argument returns
     * @return the list of {@link T}s
     */
    static <T> VariableArgument<VariableList<T>> ofList(final Argument<T> argument) {
        return (variableProcessor, pack, string)
                -> new VariableList<>(variableProcessor, pack, string, argument);
    }

    /**
     * Gets a list of {@link T}s from string.
     *
     * @param argument the argument to parse
     * @param checker  the checker to validate the list
     * @param <T>      what the argument returns
     * @return the list of {@link T}s
     */
    static <T> VariableArgument<VariableList<T>> ofList(final Argument<T> argument, final Variable.ValueChecker<List<T>> checker) {
        return (variableProcessor, pack, string)
                -> new VariableList<>(variableProcessor, pack, string, argument, checker);
    }

    /**
     * Gets a {@link T} from string.
     *
     * @param string the string to parse
     * @return the {@link T}
     * @throws QuestException when the string cannot be parsed as {@link T}
     */
    @Override
    T apply(String string) throws QuestException;
}
