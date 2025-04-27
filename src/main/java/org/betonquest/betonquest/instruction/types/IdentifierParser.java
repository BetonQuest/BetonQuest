package org.betonquest.betonquest.instruction.types;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.argument.PackageArgument;

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
        if (string.contains(".")) {
            return string;
        }
        return pack.getQuestPath() + "." + string;
    }
}
