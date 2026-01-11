package org.betonquest.betonquest.id.conversation;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.identifier.ConversationIdentifier;
import org.betonquest.betonquest.api.identifier.factory.DefaultIdentifierFactory;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * A {@link DefaultIdentifierFactory} for {@link ConversationIdentifier}s.
 */
public class ConversationIdentifierFactory extends DefaultIdentifierFactory<ConversationIdentifier> {

    /**
     * Create a new identifier factory.
     *
     * @param packManager the quest package manager to resolve relative paths
     */
    public ConversationIdentifierFactory(final QuestPackageManager packManager) {
        super(packManager);
    }

    @Override
    public ConversationIdentifier parseIdentifier(@Nullable final QuestPackage source, final String input) throws QuestException {
        final Map.Entry<QuestPackage, String> entry = parse(source, input);
        return new DefaultConversationIdentifier(entry.getKey(), entry.getValue());
    }
}
