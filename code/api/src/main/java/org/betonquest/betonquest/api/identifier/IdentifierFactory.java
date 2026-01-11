package org.betonquest.betonquest.api.identifier;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.jetbrains.annotations.Nullable;

/**
 * Factory for {@link Identifier} instances.
 *
 * @param <I> the type of the identifier
 */
@FunctionalInterface
public interface IdentifierFactory<I extends Identifier> {

    /**
     * Parses an identifier from a string using the optional source package.
     *
     * @param source the source package or null if not available
     * @param input  the input string
     * @return the parsed identifier
     * @throws QuestException if the identifier cannot be parsed
     */
    I parseIdentifier(@Nullable QuestPackage source, String input) throws QuestException;
}
