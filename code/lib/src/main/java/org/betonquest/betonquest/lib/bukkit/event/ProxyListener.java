package org.betonquest.betonquest.lib.bukkit.event;

import org.bukkit.event.Event;

/**
 * This class manages a specific {@link Event} {@link org.bukkit.event.Listener}
 * for the {@link org.betonquest.betonquest.api.bukkit.event.BukkitEventService}.
 *
 * @param <T> The event type.
 */
public interface ProxyListener<T extends Event> {

    /**
     * Returns the event type handled by this listener.
     *
     * @return The event type.
     */
    Class<T> getEventType();

    /**
     * Gets if the listener is registered to Bukkit.
     *
     * @return true if registered, false otherwise.
     */
    boolean isRegistered();

    /**
     * Registers the listener to Bukkit. Should ignore calls if the listener is already registered.
     */
    void register();

    /**
     * Unregisters the listener from Bukkit. Should ignore calls if the listener is not registered.
     */
    void unregister();
}
