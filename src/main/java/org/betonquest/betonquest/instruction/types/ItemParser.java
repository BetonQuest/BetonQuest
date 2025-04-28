package org.betonquest.betonquest.instruction.types;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.feature.FeatureAPI;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.id.ItemID;
import org.betonquest.betonquest.instruction.Item;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.argument.PackageArgument;
import org.betonquest.betonquest.instruction.variable.Variable;

/**
 * Parses a string to an item.
 */
public class ItemParser implements PackageArgument<Item> {
    /**
     * The feature API to use for parsing.
     */
    private final FeatureAPI featureAPI;

    /**
     * Creates a new parser for items.
     *
     * @param featureAPI the feature API to use for parsing
     */
    public ItemParser(final FeatureAPI featureAPI) {
        this.featureAPI = featureAPI;
    }

    @Override
    public Item apply(final QuestPackage pack, final String string) throws QuestException {
        final ItemID item;
        final Variable<Number> number;
        if (string.contains(":")) {
            final String[] parts = string.split(":", 2);
            item = new ItemID(pack, parts[0]);
            number = new Variable<>(Argument.NUMBER.apply(parts[1]));
        } else {
            item = new ItemID(pack, string);
            number = new Variable<>(1);
        }
        return new Item(featureAPI, item, number);
    }
}
