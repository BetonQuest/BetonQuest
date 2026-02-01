package org.betonquest.betonquest.id.objective;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.identifier.ObjectiveIdentifier;
import org.betonquest.betonquest.api.identifier.factory.DefaultIdentifierFactory;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * A {@link DefaultIdentifierFactory} for {@link ObjectiveIdentifier}s.
 */
public class ObjectiveIdentifierFactory extends DefaultIdentifierFactory<ObjectiveIdentifier> {

    /**
     * Create a new identifier factory.
     *
     * @param packManager the quest package manager to resolve relative paths
     */
    public ObjectiveIdentifierFactory(final QuestPackageManager packManager) {
        super(packManager, "Objective");
    }

    @Override
    public ObjectiveIdentifier parseIdentifier(@Nullable final QuestPackage source, final String input) throws QuestException {
        final Map.Entry<QuestPackage, String> entry = parse(source, input);
        final DefaultObjectiveIdentifier identifier = new DefaultObjectiveIdentifier(entry.getKey(), entry.getValue());
        return requireInstruction(identifier, DefaultObjectiveIdentifier.OBJECTIVE_SECTION);
    }
}
