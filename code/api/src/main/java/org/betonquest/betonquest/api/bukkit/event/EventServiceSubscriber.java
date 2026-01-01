package org.betonquest.betonquest.api.bukkit.event;

import org.betonquest.betonquest.api.QuestException;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;

/**
 * A subscriber for {@link Event}s in the {@link BukkitEventService}.
 *
 * @param <T> The event type.
 */
@FunctionalInterface
public interface EventServiceSubscriber<T extends Event> {

    /**
     * Called when an event of type T is fired and this subscriber is subscribed to it.
     *
     * @param event    The event.
     * @param priority The priority of the event.
     * @throws QuestException If an error occurs during the execution of the subscriber.
     */
    void call(T event, EventPriority priority) throws QuestException;
}
