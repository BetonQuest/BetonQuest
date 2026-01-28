package org.betonquest.betonquest.id.conversation;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.identifier.ConversationOptionIdentifier;
import org.betonquest.betonquest.api.identifier.factory.DefaultIdentifierFactory;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * A {@link DefaultIdentifierFactory} for {@link ConversationOptionIdentifier}s.
 */
public class ConversationOptionIdentifierFactory extends DefaultIdentifierFactory<ConversationOptionIdentifier> {

    /**
     * Create a new identifier factory.
     *
     * @param packManager the quest package manager to resolve relative paths
     */
    public ConversationOptionIdentifierFactory(final QuestPackageManager packManager) {
        super(packManager);
    }

    @Override
    public ConversationOptionIdentifier parseIdentifier(@Nullable final QuestPackage source, final String input) throws QuestException {
        final Map.Entry<QuestPackage, String> entry = parse(source, input);
        final String conversationName;
        final String optionName;
        final String[] parts = input.split("\\.", -1);
        switch (parts.length) {
            case 2 -> {
                conversationName = parts[0];
                optionName = parts[1].isEmpty() ? null : parts[1];
            }
            case 1 -> {
                conversationName = null;
                optionName = parts[0];
            }
            default -> throw new QuestException("Invalid conversation option: " + input);
        }
        return new DefaultConversationOptionIdentifier(entry.getKey(), entry.getValue(), conversationName, optionName);
    }
}
