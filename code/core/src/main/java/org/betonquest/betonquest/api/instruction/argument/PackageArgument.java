package org.betonquest.betonquest.api.instruction.argument;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.quest.Variables;

/**
 * Objectified parser for the Instruction to get a {@link T} from package and string.
 *
 * @param <T> what the argument returns
 */
@FunctionalInterface
public interface PackageArgument<T> extends InstructionArgumentParser<T> {

    /**
     * Gets a {@link T} from string.
     *
     * @param pack   the source package
     * @param string the string to parse
     * @return the {@link T}
     * @throws QuestException when the string cannot be parsed as {@link T}
     */
    T apply(QuestPackage pack, String string) throws QuestException;

    @Override
    default T apply(final Variables variables, final QuestPackageManager packManager, final QuestPackage pack, final String string) throws QuestException {
        return apply(pack, string);
    }
}
