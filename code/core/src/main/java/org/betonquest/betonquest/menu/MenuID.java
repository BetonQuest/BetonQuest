package org.betonquest.betonquest.menu;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.id.ID;
import org.jetbrains.annotations.Nullable;

/**
 * ID of a menu.
 */
public class MenuID extends ID {

    /**
     * Create a new Menu ID.
     *
     * @param pack       the package of the menu
     * @param identifier the complete identifier of the menu
     * @throws QuestException if there is no such menu
     */
    public MenuID(@Nullable final QuestPackage pack, final String identifier) throws QuestException {
        super(pack, identifier);
        if (!super.pack.getConfig().isConfigurationSection("menus." + super.getBaseID())) {
            throw new QuestException("Menu '" + getFullID() + "' is not defined");
        }
    }
}
