package org.betonquest.betonquest.kernel.registry.feature;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.message.MessageParser;
import org.betonquest.betonquest.api.message.MessageParserRegistry;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.kernel.registry.FactoryRegistry;

/**
 * A registry for message parsers.
 */
public class MessageParserRegistryImpl extends FactoryRegistry<MessageParser> implements MessageParserRegistry {
    /**
     * Create a new type registry.
     *
     * @param log the logger that will be used for logging
     */
    public MessageParserRegistryImpl(final BetonQuestLogger log) {
        super(log, "MessageParser");
    }

    @Override
    public MessageParser get(final String name) throws QuestException {
        return getFactory(name);
    }
}
