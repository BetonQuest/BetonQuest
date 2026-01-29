package org.betonquest.betonquest.id.item;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.identifier.DefaultReadableIdentifier;
import org.betonquest.betonquest.api.identifier.ItemIdentifier;

/**
 * The default implementation for {@link ItemIdentifier}s.
 */
public class DefaultItemIdentifier extends DefaultReadableIdentifier implements ItemIdentifier {

    /**
     * The section in the configuration where actions are defined.
     */
    public static final String ITEM_SECTION = "items";

    /**
     * Creates a new item identifier.
     *
     * @param pack       the package the identifier is related to.
     * @param identifier the identifier of the item.
     */
    protected DefaultItemIdentifier(final QuestPackage pack, final String identifier) {
        super(pack, identifier, ITEM_SECTION);
    }
}
