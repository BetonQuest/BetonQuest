package org.betonquest.betonquest.kernel.registry.feature;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.conversation.ConversationIOFactory;
import org.betonquest.betonquest.kernel.registry.FactoryRegistry;

/**
 * Stores the Conversation IOs that can be used in BetonQuest.
 */
public class ConversationIORegistry extends FactoryRegistry<ConversationIOFactory> {

    /**
     * Create a new ConversationIO registry.
     *
     * @param log the logger that will be used
     */
    public ConversationIORegistry(final BetonQuestLogger log) {
        super(log, "Conversation IO");
    }
}
