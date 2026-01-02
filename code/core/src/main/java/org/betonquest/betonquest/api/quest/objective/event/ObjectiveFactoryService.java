package org.betonquest.betonquest.api.quest.objective.event;

import org.betonquest.betonquest.api.logger.LogSource;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.bukkit.event.Event;

/**
 * Provides services for objective creation and event subscriptions.
 */
@FunctionalInterface
public interface ObjectiveFactoryService {

    /**
     * Requests a new event subscription using an {@link EventServiceSubscriptionBuilder}.
     * <br>
     * Calling this in the context of an {@link ObjectiveFactory} will cause
     * {@link EventServiceSubscriptionBuilder#source(LogSource)} to be called
     * with the objective's source before returning.
     * <br>
     * The request may be completed in one chain of calls requiring at least a handler and ending with
     * {@link EventServiceSubscriptionBuilder#subscribe()}
     * or {@link EventServiceSubscriptionBuilder#subscribe(boolean)}.
     *
     * @param eventClass the event class to subscribe to
     * @param <T>        the event type
     * @return a new {@link EventServiceSubscriptionBuilder} for the requested event
     */
    <T extends Event> EventServiceSubscriptionBuilder<T> request(Class<T> eventClass);
}
