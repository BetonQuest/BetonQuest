package org.betonquest.betonquest.api.instruction.argument.parser;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.identifier.Identifier;
import org.betonquest.betonquest.api.instruction.argument.InstructionArgumentParser;
import org.betonquest.betonquest.api.quest.Placeholders;

/**
 * Parses a string to an identifier.
 */
public class PackageIdentifierParser implements InstructionArgumentParser<String> {

    /**
     * The singleton instance of this parser.
     */
    public static final PackageIdentifierParser INSTANCE = new PackageIdentifierParser();

    /**
     * Created a new parser for identifiers.
     */
    public PackageIdentifierParser() {
    }

    /**
     * Overloaded by {@link #apply(Placeholders, QuestPackageManager, QuestPackage, String)}.
     *
     * @param pack   the package the instruction belongs to
     * @param string the string to parse
     * @return the parsed identifier
     */
    public String apply(final QuestPackage pack, final String string) {
        if (string.contains(Identifier.SEPARATOR)) {
            return string;
        }
        return pack.getQuestPath() + Identifier.SEPARATOR + string;
    }

    @Override
    public String apply(final Placeholders placeholders, final QuestPackageManager packManager, final QuestPackage pack, final String string) {
        return apply(pack, string);
    }
}
