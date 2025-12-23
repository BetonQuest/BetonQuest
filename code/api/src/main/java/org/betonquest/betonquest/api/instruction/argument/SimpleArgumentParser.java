package org.betonquest.betonquest.api.instruction.argument;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.quest.Variables;
import org.jetbrains.annotations.Contract;

/**
 * This interface offers a simplified version of {@link InstructionArgumentParser} to used in lambdas.
 *
 * @param <T> the type of the parsed value
 */
@FunctionalInterface
public interface SimpleArgumentParser<T> extends InstructionArgumentParser<T> {

    /**
     * Parses a {@link T} from a string without affecting the state of the {@link SimpleArgumentParser} instance.
     *
     * @param string the string to parse
     * @return the parsed {@link T} from the string
     * @throws QuestException when the string cannot be parsed as {@link T}
     */
    @Contract(pure = true)
    T apply(String string) throws QuestException;

    @Override
    default T apply(final Variables variables, final QuestPackageManager packManager, final QuestPackage pack, final String string) throws QuestException {
        return apply(string);
    }
}
