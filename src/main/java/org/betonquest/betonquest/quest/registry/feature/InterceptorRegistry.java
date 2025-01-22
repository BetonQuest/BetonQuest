package org.betonquest.betonquest.quest.registry.feature;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.conversation.Conversation;
import org.betonquest.betonquest.conversation.Interceptor;
import org.betonquest.betonquest.quest.registry.FromClassFactoryRegistry;

import java.lang.reflect.Constructor;

/**
 * Stores the Interceptors that can be used in BetonQuest.
 */
public class InterceptorRegistry extends FromClassFactoryRegistry<Interceptor, InterceptorRegistry.InterceptorFactory> {

    /**
     * Create a new Interceptor registry.
     *
     * @param log the logger that will be used for logging
     */
    public InterceptorRegistry(final BetonQuestLogger log) {
        super(log, "Interceptor");
    }

    @Override
    protected InterceptorFactory createFactory(final Class<? extends Interceptor> clazz) throws NoSuchMethodException {
        return new FactoryImpl(clazz.getConstructor(Conversation.class, OnlineProfile.class));
    }

    /**
     * Factory to create Interceptors for a conversation and online profile.
     */
    public interface InterceptorFactory {
        /**
         * Create the Interceptor.
         *
         * @param conversation  the affected conversation
         * @param onlineProfile the affected player
         * @return the created interceptor
         * @throws QuestException when the creation fails
         */
        Interceptor parse(Conversation conversation, OnlineProfile onlineProfile) throws QuestException;
    }

    /**
     * Class Constructor based implementation.
     *
     * @param constructor the used constructor
     */
    private record FactoryImpl(Constructor<? extends Interceptor> constructor) implements InterceptorFactory {

        @Override
        public Interceptor parse(final Conversation conversation, final OnlineProfile onlineProfile) throws QuestException {
            return catchConstructionException("Interceptor", constructor, conversation, onlineProfile);
        }
    }
}
