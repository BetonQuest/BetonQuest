package org.betonquest.betonquest.api.instruction.argument;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.quest.Variables;

/**
 * Objectified parser for the Instruction to get a {@link T} from quest package manager, package and string.
 *
 * @param <T> what the argument returns
 */
@FunctionalInterface
public interface IdentifierArgument<T> extends InstructionArgumentParser<T> {

    /**
     * Gets a {@link T} from string.
     *
     * @param packManager the quest package manager to get quest packages from
     * @param pack        the source package
     * @param string      the string to parse
     * @return the {@link T}
     * @throws QuestException when the string cannot be parsed as {@link T}
     */
    T apply(QuestPackageManager packManager, QuestPackage pack, String string) throws QuestException;

    @Override
    default T apply(final Variables variables, final QuestPackageManager packManager, final QuestPackage pack, final String string) throws QuestException {
        return apply(packManager, pack, string);
    }
}
