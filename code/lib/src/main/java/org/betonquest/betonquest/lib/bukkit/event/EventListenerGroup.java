package org.betonquest.betonquest.lib.bukkit.event;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.bukkit.event.EventServiceSubscriber;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Contract;

import java.util.Arrays;

/**
 * Represents a group of {@link ProxyListener}s for a specific {@link Event}.
 * It offers functions to manage and handle a bukkit event potentially for all {@link EventPriority}'s in parallel.
 *
 * @param <T> The event type.
 */
public interface EventListenerGroup<T extends Event> {

    /**
     * Called upen activation.
     * Should instantiate all listeners and make them ready to be activated.
     *
     * @param plugin the plugin that should register the listeners
     * @throws QuestException if an error occurs while instantiating the listeners
     */
    void bake(Plugin plugin) throws QuestException;

    /**
     * Get the listener for a specific priority.
     *
     * @param priority the priority to get the listener for
     * @return the listener for the priority
     */
    ProxyListener<T> getListener(EventPriority priority);

    /**
     * Calls the event for all subscribers with the given priority.
     *
     * @param event    the event to call
     * @param priority the priority of the event
     * @throws QuestException if an error occurs while calling the event
     */
    void callEvent(T event, EventPriority priority) throws QuestException;

    /**
     * Subscribe to an event with a given priority.
     *
     * @param priority        the priority to subscribe to the event with
     * @param ignoreCancelled whether canceled events should be ignored or not
     * @param subscriber      the subscriber to call when the event is fired
     * @return the subscriber that was registered
     */
    EventServiceSubscriber<T> subscribe(EventPriority priority, boolean ignoreCancelled, EventServiceSubscriber<T> subscriber);

    /**
     * Unsubscribe from an event with a given priority.
     *
     * @param priority   the priority of the event to unsubscribe from
     * @param subscriber the subscriber to unsubscribe
     */
    void unsubscribe(EventPriority priority, EventServiceSubscriber<?> subscriber);

    /**
     * Requires the event to be listened to with the given priority.
     * Essentially tells the service to prepare for subscribing to the event in advance.
     *
     * @param priority the priority to require the event to be listened to with
     * @return true if the event was successfully registered, false otherwise
     */
    boolean require(EventPriority priority);

    /**
     * Disables the event listener for a given priority in this group while retaining the subscribers.
     *
     * @param priority the priority to disable the event listener for
     */
    void disable(EventPriority priority);

    /**
     * Creates a new listener for the given event class and priority.
     * This method does not modify the state of the group.
     *
     * @param plugin     the {@link Plugin} to register the listener with
     * @param eventClass the {@link Event} class to listen to
     * @param priority   the priority to listen to the event with
     * @return a new {@link ProxyListener} for the given event class and priority
     * @throws QuestException if an error occurs while creating the listener
     */
    @Contract(value = "!null, !null, !null -> new", pure = true)
    ProxyListener<T> createListener(Plugin plugin, Class<T> eventClass, EventPriority priority) throws QuestException;

    /**
     * Disables all event listeners for all priorities in this group.
     * Essentially iterates over all priorities and calls {@link #disable(EventPriority)}.
     */
    default void disable() {
        Arrays.stream(EventPriority.values()).forEach(this::disable);
    }
}
