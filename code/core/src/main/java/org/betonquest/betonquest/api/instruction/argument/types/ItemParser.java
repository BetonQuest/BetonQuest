package org.betonquest.betonquest.api.instruction.argument.types;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.feature.FeatureApi;
import org.betonquest.betonquest.api.instruction.Item;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.instruction.argument.IdentifierArgument;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.id.ItemID;

/**
 * Parses a string to an item.
 */
public class ItemParser implements IdentifierArgument<Item> {
    /**
     * The feature API to use for parsing.
     */
    private final FeatureApi featureApi;

    /**
     * Creates a new parser for items.
     *
     * @param featureApi the feature API to use for parsing
     */
    public ItemParser(final FeatureApi featureApi) {
        this.featureApi = featureApi;
    }

    @Override
    public Item apply(final QuestPackageManager packManager, final QuestPackage pack, final String string) throws QuestException {
        final ItemID item;
        final Variable<Number> number;
        if (string.contains(":")) {
            final String[] parts = string.split(":", 2);
            item = new ItemID(packManager, pack, parts[0]);
            number = new Variable<>(Argument.NUMBER.apply(parts[1]));
        } else {
            item = new ItemID(packManager, pack, string);
            number = new Variable<>(1);
        }
        return new Item(featureApi, item, number);
    }
}
