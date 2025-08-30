package org.betonquest.betonquest.api.instruction.argument.types;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.identifier.Identifier;
import org.betonquest.betonquest.api.instruction.argument.PackageArgument;
import org.betonquest.betonquest.api.quest.QuestException;

/**
 * Parses a string to an identifier.
 */
public class IdentifierParser implements PackageArgument<String> {
    /**
     * Created a new parser for identifiers.
     */
    public IdentifierParser() {
    }

    @Override
    public String apply(final QuestPackage pack, final String string) throws QuestException {
        if (string.contains(Identifier.SEPERATOR)) {
            return string;
        }
        return pack.getQuestPath() + Identifier.SEPERATOR + string;
    }
}
