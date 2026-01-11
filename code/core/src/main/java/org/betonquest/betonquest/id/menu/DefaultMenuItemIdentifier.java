package org.betonquest.betonquest.id.menu;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.identifier.DefaultIdentifier;
import org.betonquest.betonquest.api.identifier.MenuItemIdentifier;

/**
 * The default implementation for {@link MenuItemIdentifier}s.
 */
public class DefaultMenuItemIdentifier extends DefaultIdentifier implements MenuItemIdentifier {

    /**
     * Creates a new menu item identifier.
     *
     * @param pack       the package the identifier is related to
     * @param identifier the identifier of the menu item
     */
    protected DefaultMenuItemIdentifier(final QuestPackage pack, final String identifier) {
        super(pack, identifier);
    }
}
