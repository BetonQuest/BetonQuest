package org.betonquest.betonquest.id.menu;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.identifier.MenuIdentifier;
import org.betonquest.betonquest.api.identifier.factory.DefaultIdentifierFactory;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * A {@link DefaultIdentifierFactory} for {@link MenuIdentifier}s.
 */
public class MenuIdentifierFactory extends DefaultIdentifierFactory<MenuIdentifier> {

    /**
     * Create a new identifier factory.
     *
     * @param packManager the quest package manager to resolve relative paths
     */
    public MenuIdentifierFactory(final QuestPackageManager packManager) {
        super(packManager);
    }

    @Override
    public MenuIdentifier parseIdentifier(@Nullable final QuestPackage source, final String input) throws QuestException {
        final Map.Entry<QuestPackage, String> entry = parse(source, input);
        return new DefaultMenuIdentifier(entry.getKey(), entry.getValue());
    }
}
