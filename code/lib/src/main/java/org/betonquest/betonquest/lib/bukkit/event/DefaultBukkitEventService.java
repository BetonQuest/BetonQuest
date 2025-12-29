package org.betonquest.betonquest.lib.bukkit.event;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.bukkit.event.BukkitEventService;
import org.betonquest.betonquest.api.bukkit.event.EventServiceSubscriber;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Default implementation of {@link BukkitEventService}.
 */
public class DefaultBukkitEventService implements BukkitEventService {

    /**
     * All listeners registered by this service.
     */
    private final Map<Class<? extends Event>, EventListenerGroup<?>> listeners;

    /**
     * The logger instance to use.
     */
    private final BetonQuestLogger logger;

    /**
     * The plugin instance.
     */
    private final Plugin plugin;

    /**
     * Creates a new instance of the service.
     *
     * @param plugin        The plugin instance.
     * @param loggerFactory The logger factory to use.
     */
    public DefaultBukkitEventService(final Plugin plugin, final BetonQuestLoggerFactory loggerFactory) {
        this.listeners = new HashMap<>();
        this.plugin = plugin;
        this.logger = loggerFactory.create(plugin, "EventService");
    }

    @SuppressWarnings("unchecked")
    private <T extends Event> Optional<EventListenerGroup<T>> require(final Class<T> event) {
        if (!listeners.containsKey(event)) {
            final DefaultEventListenerGroup<T> group = new DefaultEventListenerGroup<>(this.logger, event);
            try {
                group.bake(plugin);
            } catch (final QuestException e) {
                logger.error("Failed to register event listener for event " + event.getSimpleName(), e);
                return Optional.empty();
            }
            listeners.put(event, group);
        }
        return Optional.of((EventListenerGroup<T>) listeners.get(event));
    }

    @Override
    public boolean require(final Class<? extends Event> event, final EventPriority priority) {
        return require(event).map(group -> group.require(priority)).orElse(false);
    }

    @Override
    public <T extends Event> EventServiceSubscriber<T> subscribe(final Class<T> event, final EventPriority priority,
                                                                 final boolean ignoreCancelled, final EventServiceSubscriber<T> subscriber) throws QuestException {
        return require(event).map(group -> group.subscribe(priority, ignoreCancelled, subscriber))
                .orElseThrow(() -> new QuestException("Could subscribe to event " + event.getSimpleName()));
    }

    @Override
    public void unsubscribe(final Class<? extends Event> event, final EventPriority priority, final EventServiceSubscriber<?> subscriber) {
        if (!listeners.containsKey(event)) {
            return;
        }
        require(event).ifPresent(group -> group.unsubscribe(priority, subscriber));
    }
}
