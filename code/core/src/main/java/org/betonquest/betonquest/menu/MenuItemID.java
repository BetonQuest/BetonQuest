package org.betonquest.betonquest.menu;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.identifier.SectionIdentifier;
import org.jetbrains.annotations.Nullable;

/**
 * ID of a Menu item.
 */
public class MenuItemID extends SectionIdentifier {
    /**
     * Create a new Menu Item ID.
     *
     * @param packManager the quest package manager to get quest packages from
     * @param pack        the package of the menu item
     * @param identifier  the complete identifier of the menu item
     * @throws QuestException if there is no such item
     */
    public MenuItemID(final QuestPackageManager packManager, @Nullable final QuestPackage pack, final String identifier) throws QuestException {
        super(packManager, pack, identifier, "menu_items", "Menu Item");
    }
}
