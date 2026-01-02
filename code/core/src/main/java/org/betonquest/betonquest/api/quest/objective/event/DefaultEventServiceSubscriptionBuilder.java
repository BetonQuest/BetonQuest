package org.betonquest.betonquest.api.quest.objective.event;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.function.QuestFunction;
import org.betonquest.betonquest.api.logger.LogSource;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

/**
 * The default implementation of the {@link EventServiceSubscriptionBuilder}.
 * Requires a {@link StaticEventHandler} or {@link ProfileEventHandler} to be set before subscribing.
 * <br>
 * Default priority is {@link EventPriority#NORMAL}.
 * <br>
 * Default ignoreCancelled is <code>true</code>.
 *
 * @param <T> the event type
 */
public class DefaultEventServiceSubscriptionBuilder<T extends Event> implements EventServiceSubscriptionBuilder<T> {

    /**
     * The default priority for event subscriptions.
     */
    public static final EventPriority DEFAULT_PRIORITY = EventPriority.NORMAL;

    /**
     * The default ignoreCancelled flag for event subscriptions.
     */
    public static final boolean DEFAULT_IGNORE_CANCELLED = true;

    /**
     * The event service to register events with.
     */
    private final DefaultObjectiveService eventService;

    /**
     * The event class to register.
     */
    private final Class<T> eventClass;

    /**
     * The priority of the event.
     */
    private EventPriority eventPriority;

    /**
     * Whether to ignore cancelled events.
     */
    private boolean ignoreCancelled;

    /**
     * The source of the subscription for logging purposes.
     */
    private LogSource logSource;

    /**
     * The static event handler.
     */
    @Nullable
    private StaticEventHandler<T> staticHandler;

    /**
     * The profile event handler. Requires a {@link PlayerUUIDExtractor} to be set.
     */
    @Nullable
    private ProfileEventHandler<T> profileHandler;

    /**
     * The online profile event handler. Requires a {@link PlayerUUIDExtractor} to be set.
     */
    @Nullable
    private OnlineProfileEventHandler<T> onlineProfileHandler;

    /**
     * The player UUID extractor. Required for {@link ProfileEventHandler}s.
     */
    @Nullable
    private QuestFunction<T, Optional<UUID>> playerUUIDExtractor;

    /**
     * Creates a new builder for the given event class.
     *
     * @param eventService the event service to register events with
     * @param eventClass   the event class to register
     */
    public DefaultEventServiceSubscriptionBuilder(final DefaultObjectiveService eventService, final Class<T> eventClass) {
        this.eventService = eventService;
        this.eventClass = eventClass;
        this.logSource = LogSource.EMPTY;
        this.eventPriority = DEFAULT_PRIORITY;
        this.ignoreCancelled = DEFAULT_IGNORE_CANCELLED;
    }

    @Override
    public EventServiceSubscriptionBuilder<T> priority(final EventPriority priority) {
        this.eventPriority = priority;
        return this;
    }

    @Override
    public EventServiceSubscriptionBuilder<T> source(final LogSource source) {
        this.logSource = source;
        return this;
    }

    @Override
    public EventServiceSubscriptionBuilder<T> handler(final StaticEventHandler<T> handler) {
        this.staticHandler = handler;
        this.profileHandler = null;
        this.onlineProfileHandler = null;
        this.playerUUIDExtractor = null;
        return this;
    }

    @Override
    public EventServiceSubscriptionBuilder<T> handler(final ProfileEventHandler<T> handler, final PlayerUUIDExtractor<T> playerExtractor) {
        this.profileHandler = handler;
        this.playerUUIDExtractor = value -> Optional.ofNullable(playerExtractor.read(value));
        this.onlineProfileHandler = null;
        this.staticHandler = null;
        return this;
    }

    @Override
    public EventServiceSubscriptionBuilder<T> handler(final ProfileEventHandler<T> handler, final PlayerExtractor<T> playerExtractor) {
        this.profileHandler = handler;
        this.playerUUIDExtractor = value -> Optional.ofNullable(playerExtractor.read(value)).map(Player::getUniqueId);
        this.onlineProfileHandler = null;
        this.staticHandler = null;
        return this;
    }

    @Override
    public EventServiceSubscriptionBuilder<T> handler(final OnlineProfileEventHandler<T> handler, final PlayerExtractor<T> playerExtractor) {
        this.onlineProfileHandler = handler;
        this.playerUUIDExtractor = value -> Optional.ofNullable(playerExtractor.read(value)).map(Player::getUniqueId);
        this.profileHandler = null;
        this.staticHandler = null;
        return this;
    }

    @Override
    public void subscribe(final boolean ignoreCancelled) throws QuestException {
        this.ignoreCancelled = ignoreCancelled;
        subscribe();
    }

    @Override
    public void subscribe() throws QuestException {
        if (this.staticHandler != null) {
            eventService.subscribe(logSource, eventClass, staticHandler, eventPriority, ignoreCancelled);
            return;
        }
        if (playerUUIDExtractor == null) {
            throw new IllegalStateException("No valid handler specified!");
        }
        if (onlineProfileHandler != null) {
            eventService.subscribe(logSource, eventClass, onlineProfileHandler, playerUUIDExtractor, eventPriority, ignoreCancelled);
        }
        if (profileHandler != null) {
            eventService.subscribe(logSource, eventClass, profileHandler, playerUUIDExtractor, eventPriority, ignoreCancelled);
        }
    }
}
