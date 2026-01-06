package org.betonquest.betonquest.compatibility.itemsadder;

import dev.lone.itemsadder.api.CustomStack;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.argument.SimpleArgumentParser;
import org.betonquest.betonquest.util.Utils;

/**
 * Parses strings into {@link CustomStack} instances.
 */
public class ItemsAdderParser implements SimpleArgumentParser<CustomStack> {

    /**
     * The default instance of {@link ItemsAdderParser}.
     */
    public static final ItemsAdderParser ITEMS_ADDER_PARSER = new ItemsAdderParser();

    /**
 * The empty default constructor.
 */
    public ItemsAdderParser() { }

    @Override
    public CustomStack apply(final String string) throws QuestException {
        return Utils.getNN(CustomStack.getInstance(string), "Invalid ItemsAdder Item: " + string);
    }
}
