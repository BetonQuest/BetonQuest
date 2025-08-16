package org.betonquest.betonquest.menu;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.identifier.SectionIdentifier;
import org.betonquest.betonquest.api.quest.QuestException;
import org.jetbrains.annotations.Nullable;

/**
 * ID of a menu.
 */
public class MenuID extends SectionIdentifier {

    /**
     * Create a new Menu ID.
     *
     * @param pack       the package of the menu
     * @param identifier the complete identifier of the menu
     * @throws QuestException if there is no such menu
     */
    public MenuID(@Nullable final QuestPackage pack, final String identifier) throws QuestException {
        super(pack, identifier, "menus", "Menu");
    }
}
