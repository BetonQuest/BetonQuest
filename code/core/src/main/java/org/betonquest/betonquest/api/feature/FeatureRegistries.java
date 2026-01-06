package org.betonquest.betonquest.api.feature;

import org.betonquest.betonquest.api.kernel.FeatureRegistry;
import org.betonquest.betonquest.api.quest.npc.NpcRegistry;
import org.betonquest.betonquest.api.text.TextParserRegistry;
import org.betonquest.betonquest.conversation.ConversationIOFactory;
import org.betonquest.betonquest.conversation.interceptor.InterceptorFactory;
import org.betonquest.betonquest.item.ItemRegistry;
import org.betonquest.betonquest.notify.NotifyIOFactory;
import org.betonquest.betonquest.schedule.ActionScheduling;

/**
 * Provides the BetonQuest Feature Registries.
 * <p>
 * They are used to add new implementations and access them.
 */
public interface FeatureRegistries {

    /**
     * Gets the registry for conversation IOs.
     *
     * @return the conversation io registry
     */
    FeatureRegistry<ConversationIOFactory> conversationIO();

    /**
     * Gets the registry for quest items.
     *
     * @return the quest item registry
     */
    ItemRegistry item();

    /**
     * Gets the registry for chat interceptor.
     *
     * @return the interceptor registry
     */
    FeatureRegistry<InterceptorFactory> interceptor();

    /**
     * Gets the registry for text parser.
     *
     * @return the text parser registry
     */
    TextParserRegistry textParser();

    /**
     * Gets the registry for npc types.
     *
     * @return the npc registry
     */
    NpcRegistry npc();

    /**
     * Gets the registry for notify IOs.
     *
     * @return the notify io registry
     */
    FeatureRegistry<NotifyIOFactory> notifyIO();

    /**
     * Gets the registry for action scheduling types.
     *
     * @return the scheduling registry
     */
    FeatureRegistry<ActionScheduling.ScheduleType<?, ?>> actionScheduling();
}
