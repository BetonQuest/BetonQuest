package org.betonquest.betonquest.id.action;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.identifier.ActionIdentifier;
import org.betonquest.betonquest.api.identifier.factory.DefaultIdentifierFactory;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * A {@link DefaultIdentifierFactory} for {@link ActionIdentifier}s.
 */
public class ActionIdentifierFactory extends DefaultIdentifierFactory<ActionIdentifier> {

    /**
     * Create a new identifier factory.
     *
     * @param packManager the quest package manager to resolve relative paths
     */
    public ActionIdentifierFactory(final QuestPackageManager packManager) {
        super(packManager);
    }

    @Override
    public ActionIdentifier parseIdentifier(@Nullable final QuestPackage source, final String identifier) throws QuestException {
        final Map.Entry<QuestPackage, String> entry = parse(source, identifier);
        return new DefaultActionIdentifier(entry.getKey(), entry.getValue());
    }
}
