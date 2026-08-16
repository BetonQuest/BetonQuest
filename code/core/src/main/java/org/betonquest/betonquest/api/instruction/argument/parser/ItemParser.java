package org.betonquest.betonquest.api.instruction.argument.parser;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.identifier.IdentifierFactory;
import org.betonquest.betonquest.api.identifier.ItemIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Item;
import org.betonquest.betonquest.api.instruction.argument.InstructionArgumentParser;
import org.betonquest.betonquest.api.instruction.type.ItemWrapper;
import org.betonquest.betonquest.api.service.item.ItemManager;
import org.betonquest.betonquest.api.service.placeholder.PlaceholderManager;
import org.betonquest.betonquest.lib.instruction.argument.DefaultArgument;

/**
 * Parses a string to an item.
 */
public class ItemParser implements InstructionArgumentParser<ItemWrapper> {

    /**
     * The item manager to retrieve items.
     */
    private final ItemManager itemManager;

    /**
     * The identifier factory to parse {@link ItemIdentifier}s.
     */
    private final IdentifierFactory<ItemIdentifier> identifierFactory;

    /**
     * Creates a new parser for items.
     *
     * @param itemManager       the item manager to retrieve items
     * @param identifierFactory the identifier factory to parse {@link ItemIdentifier}
     */
    public ItemParser(final ItemManager itemManager,
                      final IdentifierFactory<ItemIdentifier> identifierFactory) {
        this.itemManager = itemManager;
        this.identifierFactory = identifierFactory;
    }

    @Override
    public ItemWrapper apply(final PlaceholderManager placeholders, final QuestPackageManager packManager, final QuestPackage pack, final String string) throws QuestException {
        final ItemIdentifier item;
        final Argument<Number> amount;
        if (string.contains(":")) {
            final String[] parts = string.split(":", 2);
            item = identifierFactory.parseIdentifier(pack, parts[0]);
            amount = new DefaultArgument<>(NumberParser.DEFAULT.apply(parts[1]));
        } else {
            item = identifierFactory.parseIdentifier(pack, string);
            amount = new DefaultArgument<>(1);
        }
        return new Item(itemManager, item, amount);
    }
}
