package org.betonquest.betonquest.quest.registry.feature;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.conversation.Conversation;
import org.betonquest.betonquest.conversation.ConversationIO;
import org.betonquest.betonquest.quest.registry.FromClassFactoryRegistry;

import java.lang.reflect.Constructor;

/**
 * Stores the Conversation IOs that can be used in BetonQuest.
 */
public class ConversationIORegistry extends FromClassFactoryRegistry<ConversationIO, ConversationIORegistry.ConversationIOFactory> {

    /**
     * Create a new ConversationIO registry.
     *
     * @param log the logger that will be used for logging
     */
    public ConversationIORegistry(final BetonQuestLogger log) {
        super(log, "Conversation IO");
    }

    @Override
    protected ConversationIOFactory createFactory(final Class<? extends ConversationIO> clazz) throws NoSuchMethodException {
        return new FactoryImpl(clazz.getConstructor(Conversation.class, OnlineProfile.class));
    }

    /**
     * Factory to create Conversation IO for a conversation and online profile.
     */
    public interface ConversationIOFactory {
        /**
         * Create the Conversation IO.
         *
         * @param conversation  the conversation to display
         * @param onlineProfile the player to show the conversation
         * @return the created conversation IO
         * @throws QuestException when the creation fails
         */
        ConversationIO parse(Conversation conversation, OnlineProfile onlineProfile) throws QuestException;
    }

    /**
     * Class Constructor based implementation.
     *
     * @param constructor the used constructor
     */
    private record FactoryImpl(Constructor<? extends ConversationIO> constructor) implements ConversationIOFactory {

        @Override
        public ConversationIO parse(final Conversation conversation, final OnlineProfile onlineProfile) throws QuestException {
            return catchConstructionException("Conversation IO", constructor, conversation, onlineProfile);
        }
    }
}
