package org.betonquest.betonquest.api.instruction.argument.parser;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.function.QuestBiFunction;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Item;
import org.betonquest.betonquest.api.instruction.argument.InstructionArgumentParser;
import org.betonquest.betonquest.api.instruction.type.ItemWrapper;
import org.betonquest.betonquest.api.item.QuestItem;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.Placeholders;
import org.betonquest.betonquest.id.ItemID;
import org.betonquest.betonquest.lib.instruction.argument.DefaultArgument;

/**
 * Parses a string to an item.
 */
public class ItemParser implements InstructionArgumentParser<ItemWrapper> {

    /**
     * The feature API to use for parsing.
     */
    private final QuestBiFunction<ItemID, Profile, QuestItem> getItemFunction;

    /**
     * Creates a new parser for items.
     *
     * @param getItemFunction the feature API function to retrieve items
     */
    public ItemParser(final QuestBiFunction<ItemID, Profile, QuestItem> getItemFunction) {
        this.getItemFunction = getItemFunction;
    }

    @Override
    public ItemWrapper apply(final Placeholders placeholders, final QuestPackageManager packManager, final QuestPackage pack, final String string) throws QuestException {
        final ItemID item;
        final Argument<Number> number;
        if (string.contains(":")) {
            final String[] parts = string.split(":", 2);
            item = new ItemID(placeholders, packManager, pack, parts[0]);
            number = new DefaultArgument<>(NumberParser.DEFAULT.apply(parts[1]));
        } else {
            item = new ItemID(placeholders, packManager, pack, string);
            number = new DefaultArgument<>(1);
        }
        return new Item(getItemFunction, item, number);
    }
}
