package org.betonquest.betonquest.id;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.identifier.InstructionIdentifier;
import org.betonquest.betonquest.api.quest.Placeholders;
import org.jetbrains.annotations.Nullable;

/**
 * ID of an Item.
 */
public class ItemID extends InstructionIdentifier {

    /**
     * Create a new Item ID.
     *
     * @param placeholders the {@link Placeholders} to create and resolve placeholders
     * @param packManager  the quest package manager to get quest packages from
     * @param pack         the package of the item
     * @param identifier   the complete identifier of the item
     * @throws QuestException if there is no such item
     */
    public ItemID(final Placeholders placeholders, final QuestPackageManager packManager, @Nullable final QuestPackage pack, final String identifier) throws QuestException {
        super(placeholders, packManager, pack, identifier, "items", "Item");
    }
}
