package org.betonquest.betonquest.api.instruction.argument.parser;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.identifier.Identifier;
import org.betonquest.betonquest.api.instruction.argument.PackageArgument;

/**
 * Parses a string to an identifier.
 */
public class IdentifierParser implements PackageArgument<String> {

    /**
     * The singleton instance of this parser.
     */
    public static final IdentifierParser INSTANCE = new IdentifierParser();

    /**
     * Created a new parser for identifiers.
     */
    public IdentifierParser() {
    }

    @Override
    public String apply(final QuestPackage pack, final String string) {
        if (string.contains(Identifier.SEPARATOR)) {
            return string;
        }
        return pack.getQuestPath() + Identifier.SEPARATOR + string;
    }
}
