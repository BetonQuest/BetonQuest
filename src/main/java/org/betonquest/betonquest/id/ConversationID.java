package org.betonquest.betonquest.id;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;

/**
 * Represents a conversation ID.
 */
public class ConversationID extends ID {

    /**
     * Creates new ConversationID instance.
     *
     * @param pack       the package where the identifier was used in
     * @param identifier the identifier of the conversation
     * @throws ObjectNotFoundException when the conversation could not be resolved with the given identifier
     */
    public ConversationID(final QuestPackage pack, final String identifier) throws ObjectNotFoundException {
        super(pack, identifier);
        rawInstruction = super.pack.getConfig().getString("conversations." + super.identifier);
        if (rawInstruction == null) {
            throw new ObjectNotFoundException("Conversation '" + getFullID() + "' is not defined");
        }
    }
}
