package org.betonquest.betonquest.instruction.argument;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.kernel.processor.quest.VariableProcessor;

/**
 * Objectified parser for the Instruction.
 *
 * @param <T> what the argument returns
 */
@FunctionalInterface
public interface VariableArgument<T> {

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
