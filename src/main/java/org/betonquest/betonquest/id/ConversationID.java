package org.betonquest.betonquest.id;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.identifier.SectionIdentifier;
import org.betonquest.betonquest.api.quest.QuestException;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a conversation ID.
 */
public class ConversationID extends SectionIdentifier {

    /**
     * Creates new ConversationID instance.
     *
     * @param pack       the package where the identifier was used in
     * @param identifier the identifier of the conversation
     * @throws QuestException when the conversation could not be resolved with the given identifier
     */
    public ConversationID(@Nullable final QuestPackage pack, final String identifier) throws QuestException {
        super(pack, identifier, "conversations", "Conversation");
    }
}
