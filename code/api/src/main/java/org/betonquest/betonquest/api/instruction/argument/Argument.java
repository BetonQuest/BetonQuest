package org.betonquest.betonquest.api.instruction.argument;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.instruction.variable.ValueParser;
import org.betonquest.betonquest.api.quest.Variables;
import org.jetbrains.annotations.Contract;

/**
 * Objectified parser to get a {@link T} from string.
 *
 * @param <T> the type of the parsed result
 */
@FunctionalInterface
@Deprecated
public interface Argument<T> extends ValueParser<T>, InstructionArgumentParser<T> {

    /**
     * Parses a {@link T} from a string without affecting the state of the {@link Argument} instance.
     *
     * @param string the string to parse
     * @return the parsed {@link T} from the string
     * @throws QuestException when the string cannot be parsed as {@link T}
     */
    @Override
    @Contract(pure = true)
    T apply(String string) throws QuestException;

    @Override
    default T apply(final Variables variables, final QuestPackageManager packManager, final QuestPackage pack, final String string) throws QuestException {
        return apply(string);
    }
}
