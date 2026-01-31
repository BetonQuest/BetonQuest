package org.betonquest.betonquest.id.conversation;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.identifier.ConversationIdentifier;
import org.betonquest.betonquest.api.identifier.DefaultIdentifier;

/**
 * The default implementation for {@link ConversationIdentifier}s.
 */
public class DefaultConversationIdentifier extends DefaultIdentifier implements ConversationIdentifier {

    /**
     * The section name for conversations.
     */
    public static final String CONVERSATION_SECTION = "conversations";

    /**
     * Creates a new conversation identifier.
     *
     * @param pack       the package the identifier is in
     * @param identifier the identifier of the conversation
     */
    protected DefaultConversationIdentifier(final QuestPackage pack, final String identifier) {
        super(pack, identifier);
    }
}
