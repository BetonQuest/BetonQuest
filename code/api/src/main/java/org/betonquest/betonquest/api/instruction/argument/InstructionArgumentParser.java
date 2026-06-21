package org.betonquest.betonquest.api.instruction.argument;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.service.placeholder.PlaceholderManager;
import org.jetbrains.annotations.Contract;

/**
 * This class represents a parser for any instruction arguments from a string to a value of type T.
 *
 * @param <T> the type of the parsed value
 * @since 3.0.0
 */
@FunctionalInterface
public interface InstructionArgumentParser<T> {

    /**
     * Parses the string to a value of type T optionally using all given parameters.
     *
     * @param placeholders the {@link PlaceholderManager} to create and resolve placeholders
     * @param packManager  the quest package manager to get quest packages from
     * @param pack         the package the instruction belongs to
     * @param string       the string to parse
     * @return the parsed value
     * @throws QuestException if the string cannot be parsed
     * @since 3.0.0
     */
    @Contract(pure = true)
    T apply(PlaceholderManager placeholders, QuestPackageManager packManager, QuestPackage pack, String string) throws QuestException;
}
