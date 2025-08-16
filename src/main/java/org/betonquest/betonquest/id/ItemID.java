package org.betonquest.betonquest.id;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.identifier.InstructionIdentifier;
import org.betonquest.betonquest.api.quest.QuestException;
import org.jetbrains.annotations.Nullable;

/**
 * ID of an Item.
 */
public class ItemID extends InstructionIdentifier {

    /**
     * Create a new Item ID.
     *
     * @param pack       the package of the item
     * @param identifier the complete identifier of the item
     * @throws QuestException if there is no such item
     */
    public ItemID(@Nullable final QuestPackage pack, final String identifier) throws QuestException {
        super(pack, identifier, "items", "Item");
    }
}
