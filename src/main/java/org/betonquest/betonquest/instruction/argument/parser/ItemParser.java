package org.betonquest.betonquest.instruction.argument.parser;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.id.ItemID;
import org.betonquest.betonquest.instruction.Item;
import org.betonquest.betonquest.item.QuestItem;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

/**
 * Parses QuestItems and the Item wrapper.
 */
public interface ItemParser extends IDParser {
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
    default Item[] getItemList() throws QuestException {
        return getItemList(next());
    }

    /**
     * Parses Items from the given string.
     *
     * @param string the string to parse as Items
     * @return the parsed Items or empty array if no string was provided
     * @throws QuestException when there is no part left or item parsing fails
     */
    default Item[] getItemList(@Nullable final String string) throws QuestException {
        final String[] array = getArray(string);
        final Item[] items = new Item[array.length];
        for (int i = 0; i < items.length; i++) {
            items[i] = getItem(array[i]);
        }
        return items;
    }

    /**
     * Parses {@link #getQuestItem(String)} with {@link #next()}.
     *
     * @return the parsed QuestItem
     * @throws QuestException when there is no part left or no such quest item
     */
    default QuestItem getQuestItem() throws QuestException {
        return getQuestItem(next());
    }

    /**
     * Parses the string as QuestItem.
     *
     * @param string the string to parse as QuestItem
     * @return the parsed quest item or null if no string was provided
     * @throws QuestException when there is no such quest item
     */
    @Contract("null -> null; !null -> !null")
    @Nullable
    default QuestItem getQuestItem(@Nullable final String string) throws QuestException {
        if (string == null) {
            return null;
        }
        return new QuestItem(getID(string, ItemID::new));
    }
}
