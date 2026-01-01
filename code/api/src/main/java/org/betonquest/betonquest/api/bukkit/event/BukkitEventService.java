package org.betonquest.betonquest.api.bukkit.event;

import org.betonquest.betonquest.api.QuestException;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;

/**
 * The bukkit event service allows subscribing to bukkit events without registering a listener via bukkit's api.
 * This service also allows using generics for events while maintaining a minimum number of listeners in bukkit.
 */
public interface BukkitEventService {

    /**
     * Requires an event to be listened to with a given priority.
     * Essentially tells the service to prepare for subscribing to the event in advance.
     *
     * @param event    the event to require
     * @param priority the priority to require the event to be listened to with
     * @return true if the event was successfully registered, false otherwise
     */
    boolean require(Class<? extends Event> event, EventPriority priority);

    /**
     * Subscribes to an event with a given priority.
     *
     * @param event           the event to subscribe to
     * @param priority        the priority to subscribe to the event with
     * @param ignoreCancelled whether canceled events should be ignored or not
     * @param subscriber      the subscriber to call when the event is fired
     * @param <T>             the event type
     * @return the subscriber that was registered
     * @throws QuestException if the event could not be subscribed to
     */
    <T extends Event> EventServiceSubscriber<T> subscribe(Class<T> event, EventPriority priority, boolean ignoreCancelled,
                                                          EventServiceSubscriber<T> subscriber) throws QuestException;

    /**
     * Removes a subscriber from an event.
     *
     * @param event      the event to unsubscribe from
     * @param priority   the priority of the event to unsubscribe from
     * @param subscriber the subscriber to unsubscribe
     */
    void unsubscribe(Class<? extends Event> event, EventPriority priority, EventServiceSubscriber<?> subscriber);

    /**
     * Subscribes to an event with the default ignoreCancelled value of true.
     *
     * @param event      the event to subscribe to
     * @param priority   the priority to subscribe to the event with
     * @param subscriber the subscriber to call when the event is fired
     * @param <T>        the event type
     * @return the subscriber that was registered
     * @throws QuestException if the event could not be subscribed to
     */
    default <T extends Event> EventServiceSubscriber<T> subscribe(final Class<T> event, final EventPriority priority,
                                                                  final EventServiceSubscriber<T> subscriber) throws QuestException {
        return subscribe(event, priority, true, subscriber);
    }
}
