package org.betonquest.betonquest.quest.registry.other;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.conversation.ConversationIO;
import org.betonquest.betonquest.conversation.Interceptor;
import org.betonquest.betonquest.notify.NotifyIO;
import org.betonquest.betonquest.quest.registry.FactoryRegistry;

/**
 * Registries that are not based on the Instruction to create instances.
 *
 * @param conversationIO  The Registry holding registered conversation IOs.
 * @param interceptor     The Registry holding registered Interceptors.
 * @param notifyIO        The Registry holding registered NotifyIOs.
 * @param eventScheduling The Registry holding registered Event Schedulers.
 */
public record OtherFactoryRegistries(
        FactoryRegistry<Class<? extends ConversationIO>> conversationIO,
        FactoryRegistry<Class<? extends Interceptor>> interceptor,
        FactoryRegistry<Class<? extends NotifyIO>> notifyIO,
        ScheduleRegistry eventScheduling
) {

    /**
     * Create a new other factory registry for quest elements not based on the Instruction object.
     *
     * @param loggerFactory the logger factory to create individual class logger
     * @return the newly created registries
     */
    public static OtherFactoryRegistries create(final BetonQuestLoggerFactory loggerFactory) {
        final BetonQuestLogger logger = loggerFactory.create(FactoryRegistry.class);
        return new OtherFactoryRegistries(
                new FactoryRegistry<>(logger, "Conversation IO"),
                new FactoryRegistry<>(logger, "Interceptor"),
                new FactoryRegistry<>(logger, "Notify IO"),
                new ScheduleRegistry(loggerFactory.create(ScheduleRegistry.class))
        );
    }
}
