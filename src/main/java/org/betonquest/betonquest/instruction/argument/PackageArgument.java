package org.betonquest.betonquest.instruction.argument;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.argument.types.IdentifierParser;
import org.betonquest.betonquest.instruction.argument.types.ItemParser;

/**
 * Objectified parser for the Instruction to get a {@link T} from package and string.
 *
 * @param <T> what the argument returns
 */
@FunctionalInterface
public interface PackageArgument<T> {
    /**
     * The default instance of {@link IdentifierParser}.
     */
    IdentifierParser IDENTIFIER = new IdentifierParser();

    /**
     * The default instance of {@link ItemParser}.
     */
    ItemParser ITEM = new ItemParser(BetonQuest.getInstance().getFeatureAPI());

    /**
     * Gets a {@link T} from string.
     *
     * @param pack   the source package
     * @param string the string to parse
     * @return the {@link T}
     * @throws QuestException when the string cannot be parsed as {@link T}
     */
    T apply(QuestPackage pack, String string) throws QuestException;
}
