package org.betonquest.betonquest.api.instruction.argument;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.instruction.argument.parser.ItemParser;
import org.betonquest.betonquest.api.quest.Variables;

/**
 * Objectified parser for the Instruction to get a {@link T} from variables, quest package manager, package and string.
 *
 * @param <T> what the argument returns
 */
@FunctionalInterface
public interface InstructionIdentifierArgument<T> extends InstructionArgumentParser<T> {

    /**
     * The default instance of {@link ItemParser}.
     */
    ItemParser ITEM = new ItemParser(BetonQuest.getInstance().getFeatureApi());

    @Override
    T apply(Variables variables, QuestPackageManager packManager, QuestPackage pack, String string) throws QuestException;
}
