package org.betonquest.betonquest.api.instruction.argument.parser;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.function.QuestBiFunction;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.identifier.IdentifierFactory;
import org.betonquest.betonquest.api.identifier.ItemIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Item;
import org.betonquest.betonquest.api.instruction.argument.InstructionArgumentParser;
import org.betonquest.betonquest.api.instruction.type.ItemWrapper;
import org.betonquest.betonquest.api.item.QuestItem;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.Placeholders;
import org.betonquest.betonquest.lib.instruction.argument.DefaultArgument;

/**
 * Parses a string to an item.
 */
public class ItemParser implements InstructionArgumentParser<ItemWrapper> {

    /**
     * The feature API to use for parsing.
     */
    private final QuestBiFunction<ItemIdentifier, Profile, QuestItem> getItemFunction;

    /**
     * The identifier factory to parse {@link ItemIdentifier}s.
     */
    private final IdentifierFactory<ItemIdentifier> identifierFactory;

    /**
     * Creates a new parser for items.
     *
     * @param getItemFunction   the feature API function to retrieve items
     * @param identifierFactory the identifier factory to parse {@link ItemIdentifier}
     */
    public ItemParser(final QuestBiFunction<ItemIdentifier, Profile, QuestItem> getItemFunction,
                      final IdentifierFactory<ItemIdentifier> identifierFactory) {
        this.getItemFunction = getItemFunction;
        this.identifierFactory = identifierFactory;
    }

    @Override
    public ItemWrapper apply(final Placeholders placeholders, final QuestPackageManager packManager, final QuestPackage pack, final String string) throws QuestException {
        final ItemIdentifier item;
        final Argument<Number> number;
        if (string.contains(":")) {
            final String[] parts = string.split(":", 2);
            item = identifierFactory.parseIdentifier(pack, parts[0]);
            number = new DefaultArgument<>(NumberParser.DEFAULT.apply(parts[1]));
        } else {
            item = identifierFactory.parseIdentifier(pack, string);
            number = new DefaultArgument<>(1);
        }
        return new Item(getItemFunction, item, number);
    }
}
