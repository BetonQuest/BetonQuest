package org.betonquest.betonquest.quest.registry.other;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.conversation.ConversationIO;
import org.betonquest.betonquest.conversation.Interceptor;
import org.betonquest.betonquest.notify.NotifyIO;
import org.betonquest.betonquest.quest.registry.FactoryRegistry;

/**
 * Registries that are not based on the Instruction to create instances.
 */
@SuppressWarnings("PMD.DataClass")
public class OtherFactoryRegistries {
    /**
     * ConversationIO registry.
     */
    private final FactoryRegistry<Class<? extends ConversationIO>> conversationIOTypes;

    /**
     * Interceṕtor registry.
     */
    private final FactoryRegistry<Class<? extends Interceptor>> interceptorTypes;

    /**
     * NotifyIO registry.
     */
    private final FactoryRegistry<Class<? extends NotifyIO>> notifyIOTypes;

    /**
     * Event Scheduling registry.
     */
    private final ScheduleRegistry eventSchedulingTypes;

    /**
     * Create a new other factory registry for quest elements not based on the Instruction object.
     *
     * @param loggerFactory the logger factory to create individual class logger
     */
    public OtherFactoryRegistries(final BetonQuestLoggerFactory loggerFactory) {
        final BetonQuestLogger logger = loggerFactory.create(FactoryRegistry.class);
        this.conversationIOTypes = new FactoryRegistry<>(logger, "Conversation IO");
        this.interceptorTypes = new FactoryRegistry<>(logger, "Interceptor");
        this.notifyIOTypes = new FactoryRegistry<>(logger, "Notify IO");
        this.eventSchedulingTypes = new ScheduleRegistry(loggerFactory.create(ScheduleRegistry.class));
    }

    /**
     * Gets the Registry holding registered conversation IOs.
     *
     * @return registry containing usable conversation IOs
     */
    public FactoryRegistry<Class<? extends ConversationIO>> getConversationIOTypes() {
        return conversationIOTypes;
    }

    /**
     * Gets the Registry holding registered Interceptors.
     *
     * @return registry containing usable interceptors
     */
    public FactoryRegistry<Class<? extends Interceptor>> getInterceptorTypes() {
        return interceptorTypes;
    }

    /**
     * Gets the Registry holding registered NotifyIOs.
     *
     * @return registry containing usable notify IOs
     */
    public FactoryRegistry<Class<? extends NotifyIO>> getNotifyIOTypes() {
        return notifyIOTypes;
    }

    /**
     * Gets the Registry holding registered Event Schedulers.
     *
     * @return registry containing usable event schedulers
     */
    public ScheduleRegistry getEventSchedulingTypes() {
        return eventSchedulingTypes;
    }
}
