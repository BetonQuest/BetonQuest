package org.betonquest.betonquest.api.quest.objective.event;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.function.QuestBiFunction;
import org.betonquest.betonquest.api.common.function.QuestFunction;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.objective.ObjectiveID;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

/**
 * The default implementation of the {@link EventServiceSubscriptionBuilder}.
 * Requires a {@link NonProfileEventHandler} or {@link ProfileEventHandler} to be set before subscribing.
 * <br>
 * Default priority is {@link EventPriority#NORMAL}.
 * <br>
 * Default ignoreCancelled is <code>true</code>.
 *
 * @param <T> the event type
 */
@SuppressWarnings({"PMD.TooManyMethods", "PMD.AvoidDuplicateLiterals"})
public class DefaultEventServiceSubscriptionBuilder<T extends Event> implements EventServiceSubscriptionBuilder<T> {

    /**
     * The default priority for event subscriptions.
     */
    public static final EventPriority DEFAULT_PRIORITY = EventPriority.NORMAL;

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
     * Whether to ignore conditions.
     */
    private boolean conditionsIgnore;

    /**
     * The objective related to this event.
     */
    @Nullable
    private ObjectiveID objectiveID;

    /**
     * The non-profile event handler.
     */
    @Nullable
    private NonProfileEventHandler<T> nonProfileHandler;

    /**
     * The profile event handler. Requires an extractor to be set.
     */
    @Nullable
    private ProfileEventHandler<T> profileHandler;

    /**
     * The online profile event handler. Requires an extractor to be set.
     */
    @Nullable
    private OnlineProfileEventHandler<T> onlineProfileHandler;

    /**
     * The player UUID extractor. Required for {@link ProfileEventHandler}s.
     */
    @Nullable
    private QuestBiFunction<ProfileProvider, T, Optional<Profile>> profileExtractor;

    /**
     * Creates a new builder for the given event class.
     *
     * @param eventService the event service to register events with
     * @param eventClass   the event class to register
     */
    public DefaultEventServiceSubscriptionBuilder(final DefaultObjectiveService eventService, final Class<T> eventClass) {
        this.eventService = eventService;
        this.eventClass = eventClass;
        this.objectiveID = null;
        this.conditionsIgnore = false;
        this.eventPriority = DEFAULT_PRIORITY;
    }

    @Override
    public EventServiceSubscriptionBuilder<T> priority(final EventPriority priority) {
        this.eventPriority = priority;
        return this;
    }

    @Override
    public EventServiceSubscriptionBuilder<T> source(final ObjectiveID objectiveID) {
        this.objectiveID = objectiveID;
        return this;
    }

    @Override
    public EventServiceSubscriptionBuilder<T> handler(final NonProfileEventHandler<T> handler) {
        if (checkHandlerAlreadySet()) {
            throw new IllegalStateException("Cannot set more than one handler!");
        }
        this.nonProfileHandler = handler;
        return this;
    }

    @Override
    public EventServiceSubscriptionBuilder<T> handler(final ProfileEventHandler<T> handler) {
        if (checkHandlerAlreadySet()) {
            throw new IllegalStateException("Cannot set more than one handler!");
        }
        this.profileHandler = handler;
        return this;
    }

    @Override
    public EventServiceSubscriptionBuilder<T> onlineHandler(final OnlineProfileEventHandler<T> handler) {
        if (checkHandlerAlreadySet()) {
            throw new IllegalStateException("Cannot set more than one handler!");
        }
        this.onlineProfileHandler = handler;
        return this;
    }

    @Override
    public EventServiceSubscriptionBuilder<T> uuid(final QuestFunction<T, UUID> extractor) {
        if (this.profileExtractor != null) {
            throw new IllegalStateException("Cannot set more than one extractor!");
        }
        this.profileExtractor = (provider, event) -> Optional.ofNullable(provider.getProfile(extractor.apply(event)));
        return this;
    }

    @Override
    public EventServiceSubscriptionBuilder<T> offlinePlayer(final QuestFunction<T, OfflinePlayer> extractor) {
        if (this.profileExtractor != null) {
            throw new IllegalStateException("Cannot set more than one extractor!");
        }
        this.profileExtractor = (provider, event) -> Optional.ofNullable(provider.getProfile(extractor.apply(event)));
        return this;
    }

    @Override
    public EventServiceSubscriptionBuilder<T> player(final QuestFunction<T, Player> extractor) {
        if (this.profileExtractor != null) {
            throw new IllegalStateException("Cannot set more than one extractor!");
        }
        this.profileExtractor = (provider, event) -> Optional.ofNullable(provider.getProfile(extractor.apply(event)));
        return this;
    }

    @Override
    public EventServiceSubscriptionBuilder<T> entity(final QuestFunction<T, Entity> extractor) {
        if (this.profileExtractor != null) {
            throw new IllegalStateException("Cannot set more than one extractor!");
        }
        this.profileExtractor = (provider, event) ->
                extractor.apply(event) instanceof final Player player
                        ? Optional.ofNullable(provider.getProfile(player)) : Optional.empty();
        return this;
    }

    @Override
    public EventServiceSubscriptionBuilder<T> profile(final QuestFunction<T, Profile> extractor) {
        if (this.profileExtractor != null) {
            throw new IllegalStateException("Cannot set more than one extractor!");
        }
        this.profileExtractor = (provider, event) -> Optional.ofNullable(extractor.apply(event));
        return this;
    }

    @Override
    public EventServiceSubscriptionBuilder<T> ignoreConditions() {
        this.conditionsIgnore = true;
        return this;
    }

    @Override
    public void subscribe(final boolean ignoreCancelled) throws QuestException {
        this.ignoreCancelled = ignoreCancelled;
        subscribe();
    }

    private void subscribe() throws QuestException {
        if (this.objectiveID == null) {
            throw new IllegalStateException("No objective ID specified!");
        }
        if (this.nonProfileHandler != null) {
            eventService.subscribe(objectiveID, eventClass, nonProfileHandler, eventPriority, ignoreCancelled, conditionsIgnore);
            return;
        }
        if (profileExtractor == null) {
            throw new IllegalStateException("No valid extractor specified!");
        }
        if (onlineProfileHandler != null) {
            eventService.subscribe(objectiveID, eventClass, onlineProfileHandler, profileExtractor, eventPriority, ignoreCancelled, conditionsIgnore);
            return;
        }
        if (profileHandler != null) {
            eventService.subscribe(objectiveID, eventClass, profileHandler, profileExtractor, eventPriority, ignoreCancelled, conditionsIgnore);
            return;
        }
        throw new IllegalStateException("No valid handler specified!");
    }

    private boolean checkHandlerAlreadySet() {
        return nonProfileHandler != null || profileHandler != null || onlineProfileHandler != null;
    }
}
