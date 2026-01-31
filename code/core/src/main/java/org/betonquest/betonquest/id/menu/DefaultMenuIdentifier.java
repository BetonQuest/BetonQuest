package org.betonquest.betonquest.id.menu;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.identifier.DefaultIdentifier;
import org.betonquest.betonquest.api.identifier.MenuIdentifier;

/**
 * The default implementation for {@link MenuIdentifier}s.
 */
public class DefaultMenuIdentifier extends DefaultIdentifier implements MenuIdentifier {

    /**
     * The section name for menus.
     */
    public static final String MENU_SECTION = "menus";

    /**
     * Creates a new menu identifier.
     *
     * @param pack       the package the identifier is related to
     * @param identifier the identifier of the menu
     */
    protected DefaultMenuIdentifier(final QuestPackage pack, final String identifier) {
        super(pack, identifier);
    }
}
