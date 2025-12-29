package org.betonquest.betonquest.lib.bukkit.event;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.bukkit.event.EventServiceSubscriber;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.LogSource;
import org.betonquest.betonquest.lib.logger.QuestExceptionHandler;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * The default implementation of {@link EventListenerGroup}.
 *
 * @param <T> the event type
 */
public class DefaultEventListenerGroup<T extends Event> implements EventListenerGroup<T> {

    /**
     * A no-op listener.
     */
    private static final Listener NO_OP_LISTENER = new Listener() {
    };

    /**
     * The event class.
     */
    private final Class<T> eventClass;

    /**
     * All {@link ProxyListener}s for the event class for each priority.
     */
    private final Map<EventPriority, ProxyListener<T>> listeners;

    /**
     * The subscribers to call when an event is fired.
     */
    private final Map<EventPriority, List<EventServiceSubscriber<T>>> subscribers;

    /**
     * The logger to use.
     */
    private final BetonQuestLogger log;

    /**
     * Default constructor.
     *
     * @param logger     the logger to use
     * @param eventClass the event class to listen to
     */
    public DefaultEventListenerGroup(final BetonQuestLogger logger, final Class<T> eventClass) {
        this.listeners = new EnumMap<>(EventPriority.class);
        this.subscribers = new EnumMap<>(EventPriority.class);
        this.log = logger;
        this.eventClass = eventClass;
    }

    @Override
    public void bake(final Plugin plugin) throws QuestException {
        for (final EventPriority priority : EventPriority.values()) {
            this.listeners.put(priority, createListener(plugin, eventClass, priority));
            this.subscribers.put(priority, new ArrayList<>());
        }
    }

    @Override
    public ProxyListener<T> getListener(final EventPriority priority) {
        final ProxyListener<T> listener = listeners.get(priority);
        if (listener == null) {
            throw new IllegalStateException("Cannot find listener. No listener registered for event %s and priority %s"
                    .formatted(eventClass.getSimpleName(), priority.name()));
        }
        return listener;
    }

    @Override
    public void callEvent(final T event, final EventPriority priority) throws QuestException {
        final List<EventServiceSubscriber<T>> subscriberList = this.subscribers.getOrDefault(priority, Collections.emptyList());
        for (final EventServiceSubscriber<T> subscriber : subscriberList) {
            subscriber.call(event, priority);
        }
    }

    @Override
    public EventServiceSubscriber<T> subscribe(final EventPriority priority, final boolean ignoreCancelled, final EventServiceSubscriber<T> subscriber) {
        final List<EventServiceSubscriber<T>> subscriberList = subscribers.computeIfAbsent(priority, p -> new ArrayList<>());
        if (ignoreCancelled) {
            final EventServiceSubscriber<T> decoratedSubscriber = (event, prio) -> {
                if (event instanceof final Cancellable cancellable && cancellable.isCancelled()) {
                    return;
                }
                subscriber.call(event, prio);
            };
            subscriberList.add(decoratedSubscriber);
            return decoratedSubscriber;
        }
        subscriberList.add(subscriber);
        return subscriber;
    }

    @Override
    public void unsubscribe(final EventPriority priority, final EventServiceSubscriber<?> subscriber) {
        final List<EventServiceSubscriber<T>> list = subscribers.get(priority);
        if (list == null || !list.remove(subscriber)) {
            this.log.warn("Subscriber %s not found for event %s and priority %s yet has been attempted to be unsubscribed."
                    .formatted(subscriber, eventClass.getSimpleName(), priority.name()));
        }
    }

    @Override
    public boolean require(final EventPriority priority) {
        final ProxyListener<T> listener = listeners.get(priority);
        if (listener == null) {
            this.log.error("No listener registered for event %s and priority %s"
                    .formatted(eventClass.getSimpleName(), priority.name()));
            return false;
        }
        if (listener.isRegistered()) {
            return true;
        }
        listener.register();
        return listener.isRegistered();
    }

    @Override
    public void disable(final EventPriority priority) {
        final ProxyListener<T> listener = listeners.get(priority);
        if (listener == null) {
            this.log.error("Disabling failed. No listener registered for event %s and priority %s"
                    .formatted(eventClass.getSimpleName(), priority.name()));
            return;
        }
        listener.unregister();
    }

    @Override
    public ProxyListener<T> createListener(final Plugin plugin, final Class<T> eventClass, final EventPriority priority) throws QuestException {
        final QuestExceptionHandler handler = new QuestExceptionHandler(LogSource.EMPTY, log, eventClass.getSimpleName(), priority.name());
        final RegisteredListener registeredListener = new RegisteredListener(NO_OP_LISTENER,
                (l, e) -> handler.handle(() -> callEvent(eventClass.cast(e), priority)), priority, plugin, false);
        return new DefaultProxyListener<>(eventClass, registeredListener);
    }
}
