package org.betonquest.betonquest.menu;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.identifier.SectionIdentifier;
import org.betonquest.betonquest.api.quest.QuestException;
import org.jetbrains.annotations.Nullable;

/**
 * ID of a Menu item.
 */
public class MenuItemID extends SectionIdentifier {
    /**
     * Create a new Menu Item ID.
     *
     * @param questPackageManager the quest package manager to use for the instruction
     * @param pack                the package of the menu item
     * @param identifier          the complete identifier of the menu item
     * @throws QuestException if there is no such item
     */
    public MenuItemID(final QuestPackageManager questPackageManager, @Nullable final QuestPackage pack, final String identifier) throws QuestException {
        super(questPackageManager, pack, identifier, "menu_items", "Menu Item");
    }
}
