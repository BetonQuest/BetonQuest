package org.betonquest.betonquest.instruction.argument.parser;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.Item;
import org.betonquest.betonquest.instruction.variable.VariableList;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

/**
 * Parses QuestItems and the Item wrapper.
 */
public interface ItemParser extends Parser {
    /**
     * Parses {@link #getItem(String)} with {@link #next()}.
     *
     * @return the parsed Item
     * @throws QuestException when there is no part left or item parsing fails
     */
    default Item getItem() throws QuestException {
        return getItem(next());
    }

    /**
     * Parses the string as Item.
     *
     * @param string the string to parse as Item
     * @return the parsed Item or null if no string was provided
     * @throws QuestException when item parsing fails
     */
    @Contract("null -> null; !null -> !null")
    @Nullable
    Item getItem(@Nullable String string) throws QuestException;

    /**
     * Parses {@link #getItemList(String)} with {@link #next()}.
     *
     * @return the parsed IDs
     * @throws QuestException when there is no part left or item parsing fails
     */
    default VariableList<Item> getItemList() throws QuestException {
        return getItemList(next());
    }

    /**
     * Parses Items from the given string.
     *
     * @param string the string to parse as Items
     * @return the parsed Items or empty list if no string was provided
     * @throws QuestException when there is no part left or item parsing fails
     */
    VariableList<Item> getItemList(@Nullable String string) throws QuestException;
}
