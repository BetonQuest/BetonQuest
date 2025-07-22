package org.betonquest.betonquest.menu;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.id.ID;
import org.jetbrains.annotations.Nullable;

/**
 * ID of a Menu item.
 */
public class MenuItemID extends ID {
    /**
     * Create a new Menu Item ID.
     *
     * @param pack       the package of the menu item
     * @param identifier the complete identifier of the menu item
     * @throws QuestException if there is no such item
     */
    public MenuItemID(@Nullable final QuestPackage pack, final String identifier) throws QuestException {
        super(pack, identifier);
        if (!super.pack.getConfig().isConfigurationSection("menu_items." + super.getBaseID())) {
            throw new QuestException("Menu Item '" + getFullID() + "' is not defined");
        }
    }
}
