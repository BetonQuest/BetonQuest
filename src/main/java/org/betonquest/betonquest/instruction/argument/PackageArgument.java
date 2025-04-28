package org.betonquest.betonquest.instruction.argument;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.ValueChecker;
import org.betonquest.betonquest.instruction.types.IdentifierParser;
import org.betonquest.betonquest.instruction.variable.VariableList;

import java.util.List;

/**
 * Objectified parser for the Instruction to get a {@link T} from package and string.
 *
 * @param <T> what the argument returns
 */
@FunctionalInterface
public interface PackageArgument<T> {
    /**
     * The default instance of {@link IdentifierParser}.
     */
    IdentifierParser IDENTIFIER = new IdentifierParser();

    /**
     * Gets a list of {@link T}s from string.
     *
     * @param argument the argument to parse
     * @param <T>      what the argument returns
     * @return the list of {@link T}s
     */
    static <T> VariableArgument<VariableList<T>> ofList(final PackageArgument<T> argument) {
        return ofList(argument, list -> {
        });
    }

    /**
     * Gets a list of {@link T} from string.
     *
     * @param argument the argument to parse
     * @param checker  the checker to validate the list
     * @param <T>      what the argument returns
     * @return the list of {@link T}s
     */
    static <T> VariableArgument<VariableList<T>> ofList(final PackageArgument<T> argument, final ValueChecker<List<T>> checker) {
        return (variableProcessor, pack, string)
                -> new VariableList<>(variableProcessor, pack, string, value -> argument.apply(pack, value), checker);
    }

    /**
     * Gets a {@link T} from string.
     *
     * @param pack   the source package
     * @param string the string to parse
     * @return the {@link T}
     * @throws QuestException when the string cannot be parsed as {@link T}
     */
    T apply(QuestPackage pack, String string) throws QuestException;
}
