package org.betonquest.betonquest.api.service.identifier;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.Identifier;
import org.betonquest.betonquest.api.identifier.IdentifierFactory;
import org.jetbrains.annotations.Contract;

/**
 * A registry for identifier factories.
 *
 * @see IdentifierFactory
 * @since 3.0.0
 */
public interface Identifiers {

    /**
     * Registers a new identifier factory for the specified identifier type.
     *
     * @param identifierClazz the class of the identifier to register a factory for
     * @param factory         the factory to create identifiers of the given type
     * @param <I>             the type of the identifier
     * @since 3.0.0
     */
    @Contract(mutates = "this")
    <I extends Identifier> void register(Class<I> identifierClazz, IdentifierFactory<I> factory);

    /**
     * Returns the stored factory for the specified identifier type.
     *
     * @param clazz the class of the identifier to retrieve the factory for
     * @param <I>   the type of the identifier
     * @return the factory instance
     * @throws QuestException if no factory is registered for the given type
     * @since 3.0.0
     */
    @Contract(pure = true)
    <I extends Identifier> IdentifierFactory<I> getFactory(Class<I> clazz) throws QuestException;
}
