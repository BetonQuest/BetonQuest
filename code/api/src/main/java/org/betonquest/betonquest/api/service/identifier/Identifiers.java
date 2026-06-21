package org.betonquest.betonquest.api.service.identifier;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.Identifier;
import org.betonquest.betonquest.api.identifier.IdentifierFactory;
import org.jetbrains.annotations.Contract;

/**
 * The registry for identifier factories.
 *
 * @see IdentifierFactory
 * @since 3.0.0
 */
public interface Identifiers {

    /**
     * Registers a new identifier factory.
     *
     * @param identifierClazz the type of the identifier to register a factory for
     * @param factory         the identifier factory to create the type
     * @param <I>             the type of the identifier
     * @since 3.0.0
     */
    @Contract(mutates = "this")
    <I extends Identifier> void register(Class<I> identifierClazz, IdentifierFactory<I> factory);

    /**
     * Fetches the stored factory for the given type.
     *
     * @param clazz the type to fetch the factory for
     * @param <I>   the type
     * @return a factory to create the type
     * @throws QuestException when there is no stored type
     * @since 3.0.0
     */
    @Contract(pure = true)
    <I extends Identifier> IdentifierFactory<I> getFactory(Class<I> clazz) throws QuestException;
}
