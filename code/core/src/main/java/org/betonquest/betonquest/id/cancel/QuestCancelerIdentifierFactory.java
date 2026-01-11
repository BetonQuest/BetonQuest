package org.betonquest.betonquest.id.cancel;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.identifier.QuestCancelerIdentifier;
import org.betonquest.betonquest.api.identifier.factory.DefaultIdentifierFactory;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * A {@link DefaultIdentifierFactory} for {@link QuestCancelerIdentifier}s.
 */
public class QuestCancelerIdentifierFactory extends DefaultIdentifierFactory<QuestCancelerIdentifier> {

    /**
     * Create a new identifier factory.
     *
     * @param packManager the quest package manager to resolve relative paths
     */
    public QuestCancelerIdentifierFactory(final QuestPackageManager packManager) {
        super(packManager);
    }

    @Override
    public QuestCancelerIdentifier parseIdentifier(@Nullable final QuestPackage source, final String input) throws QuestException {
        final Map.Entry<QuestPackage, String> entry = parse(source, input);
        return new DefaultQuestCancelerIdentifier(entry.getKey(), entry.getValue());
    }
}
