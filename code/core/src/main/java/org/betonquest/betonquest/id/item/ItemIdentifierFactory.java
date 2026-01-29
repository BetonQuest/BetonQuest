package org.betonquest.betonquest.id.item;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.identifier.ItemIdentifier;
import org.betonquest.betonquest.api.identifier.factory.DefaultIdentifierFactory;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * A {@link DefaultIdentifierFactory} for {@link ItemIdentifier}s.
 */
public class ItemIdentifierFactory extends DefaultIdentifierFactory<ItemIdentifier> {

    /**
     * Create a new identifier factory.
     *
     * @param packManager the quest package manager to resolve relative paths
     */
    public ItemIdentifierFactory(final QuestPackageManager packManager) {
        super(packManager);
    }

    @Override
    public ItemIdentifier parseIdentifier(@Nullable final QuestPackage source, final String input) throws QuestException {
        final Map.Entry<QuestPackage, String> entry = parse(source, input);
        final DefaultItemIdentifier identifier = new DefaultItemIdentifier(entry.getKey(), entry.getValue());
        return requireInstruction(identifier, DefaultItemIdentifier.ITEM_SECTION);
    }
}
