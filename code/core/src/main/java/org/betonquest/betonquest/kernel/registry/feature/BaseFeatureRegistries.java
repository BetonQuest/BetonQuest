package org.betonquest.betonquest.kernel.registry.feature;

import org.betonquest.betonquest.api.feature.FeatureRegistries;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.text.TextParserRegistry;
import org.betonquest.betonquest.kernel.registry.FactoryRegistry;
import org.betonquest.betonquest.kernel.registry.quest.NpcTypeRegistry;

/**
 * Registries that are not based on the Instruction to create instances.
 *
 * @param conversationIO  The Registry holding registered conversation IOs.
 * @param item            The Registry holding registered item types.
 * @param interceptor     The Registry holding registered Interceptors.
 * @param textParser      The Registry holding registered text Parsers.
 * @param npc             The Registry holding registered npc types.
 * @param notifyIO        The Registry holding registered NotifyIOs.
 * @param eventScheduling The Registry holding registered Event Schedulers.
 */
public record BaseFeatureRegistries(
        ConversationIORegistry conversationIO,
        ItemTypeRegistry item,
        InterceptorRegistry interceptor,
        TextParserRegistry textParser,
        NpcTypeRegistry npc,
        NotifyIORegistry notifyIO,
        ScheduleRegistry eventScheduling
) implements FeatureRegistries {

    /**
     * Create a new other factory registry for quest elements not based on the Instruction object.
     *
     * @param loggerFactory the logger factory to create individual class logger
     * @return the newly created registries
     */
    public static BaseFeatureRegistries create(final BetonQuestLoggerFactory loggerFactory) {
        return new BaseFeatureRegistries(
                new ConversationIORegistry(loggerFactory.create(ConversationIORegistry.class)),
                new ItemTypeRegistry(loggerFactory.create(ItemTypeRegistry.class)),
                new InterceptorRegistry(loggerFactory.create(FactoryRegistry.class)),
                new TextParserRegistryImpl(loggerFactory.create(TextParserRegistryImpl.class)),
                new NpcTypeRegistry(loggerFactory.create(NpcTypeRegistry.class)),
                new NotifyIORegistry(loggerFactory.create(NotifyIORegistry.class)),
                new ScheduleRegistry(loggerFactory.create(ScheduleRegistry.class))
        );
    }
}
