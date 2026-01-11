package org.betonquest.betonquest.id.menu;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.identifier.MenuItemIdentifier;
import org.betonquest.betonquest.api.identifier.factory.DefaultIdentifierFactory;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * A {@link DefaultIdentifierFactory} for {@link MenuItemIdentifier}s.
 */
public class MenuItemIdentifierFactory extends DefaultIdentifierFactory<MenuItemIdentifier> {

    /**
     * Create a new identifier factory.
     *
     * @param packManager the quest package manager to resolve relative paths
     */
    public MenuItemIdentifierFactory(final QuestPackageManager packManager) {
        super(packManager);
    }

    @Override
    public MenuItemIdentifier parseIdentifier(@Nullable final QuestPackage source, final String input) throws QuestException {
        final Map.Entry<QuestPackage, String> entry = parse(source, input);
        return new DefaultMenuItemIdentifier(entry.getKey(), entry.getValue());
    }
}
