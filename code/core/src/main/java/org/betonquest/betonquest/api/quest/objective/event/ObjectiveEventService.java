package org.betonquest.betonquest.api.quest.objective.event;

import org.bukkit.event.Event;

/**
 * The event service for objectives managing the subscription of event handlers.
 */
public interface ObjectiveEventService {

    /**
     * Requests a new event subscription using an {@link EventServiceSubscriptionBuilder}.
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
