package org.betonquest.betonquest.api.instruction.argument.parser;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.identifier.Identifier;
import org.betonquest.betonquest.api.identifier.factory.IdentifierRegistry;
import org.betonquest.betonquest.api.instruction.argument.InstructionArgumentParser;
import org.betonquest.betonquest.api.quest.Placeholders;

/**
 * Parses a string to an identifier of the specified type I.
 *
 * @param <I> the type of identifier to parse
 */
public class IdentifierParser<I extends Identifier> implements InstructionArgumentParser<I> {

    /**
     * The identifier registry to use.
     */
    private final IdentifierRegistry registry;

    /**
     * The identifier class to parse.
     */
    private final Class<I> identifierClass;

    /**
     * Creates a new parser for the specified identifier class.
     *
     * @param registry        the identifier registry to use
     * @param identifierClass the identifier class to parse
     */
    public IdentifierParser(final IdentifierRegistry registry, final Class<I> identifierClass) {
        this.identifierClass = identifierClass;
        this.registry = registry;
    }

    @Override
    public I apply(final Placeholders placeholders, final QuestPackageManager packManager, final QuestPackage pack, final String string) throws QuestException {
        return registry.getFactory(identifierClass).parseIdentifier(pack, string);
    }
}
