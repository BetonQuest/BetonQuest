package org.betonquest.betonquest.api.instruction.argument;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.quest.Variables;

/**
 * This class represents a parser for any instruction arguments from a string to a value of type T.
 *
 * @param <T> the type of the parsed value
 */
public interface InstructionArgumentParser<T> {

    /**
     * Parses the string to a value of type T.
     *
     * @param variables   the interface providing access to variables
     * @param packManager the quest package manager to get quest packages from
     * @param pack        the package the instruction belongs to
     * @param string      the string to parse
     * @return the parsed value
     * @throws QuestException if the string cannot be parsed
     */
    T apply(Variables variables, QuestPackageManager packManager, QuestPackage pack, String string) throws QuestException;
}
