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
        super(packManager, "Action");
    }

    @Override
    public ActionIdentifier parseIdentifier(@Nullable final QuestPackage source, final String input) throws QuestException {
        final Map.Entry<QuestPackage, String> entry = parse(source, input);
        final DefaultActionIdentifier identifier = new DefaultActionIdentifier(entry.getKey(), entry.getValue());
        return requireInstruction(identifier, DefaultActionIdentifier.ACTION_SECTION);
    }
}
