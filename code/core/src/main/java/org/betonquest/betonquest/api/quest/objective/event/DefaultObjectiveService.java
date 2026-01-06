package org.betonquest.betonquest.api.quest.objective.event;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.bukkit.event.BukkitEventService;
import org.betonquest.betonquest.api.bukkit.event.EventServiceSubscriber;
import org.betonquest.betonquest.api.common.function.QuestFunction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.logger.LogSource;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.objective.ObjectiveID;
import org.betonquest.betonquest.lib.bukkit.event.DefaultBukkitEventService;
import org.betonquest.betonquest.lib.logger.QuestExceptionHandler;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.Plugin;

import java.util.Optional;
import java.util.UUID;

/**
 * The default implementation of the {@link ObjectiveService}.
 */
@SuppressWarnings("PMD.CouplingBetweenObjects")
public class DefaultObjectiveService implements ObjectiveService {

    /**
     * The event service to register events with.
     */
    private final BukkitEventService eventService;

    /**
     * The profile provider to get the profile of a player.
     */
    private final ProfileProvider profileProvider;

    /**
     * The logger for this service.
     */
    private final BetonQuestLogger logger;

    /**
     * Sole constructor. Creates an objective event service on top of a {@link BukkitEventService}.
     *
     * @param plugin          the plugin instance
     * @param factory         the logger factory
     * @param profileProvider the profile provider
     */
    public DefaultObjectiveService(final Plugin plugin, final BetonQuestLoggerFactory factory, final ProfileProvider profileProvider) {
        this.eventService = new DefaultBukkitEventService(plugin, factory);
        this.logger = factory.create(DefaultObjectiveService.class);
        this.profileProvider = profileProvider;
    }

    @Override
    public ObjectiveFactoryService getSubscriptionService(final ObjectiveID objectiveID) {
        return new DefaultObjectiveFactoryService(objectiveID.getPackage(), this);
    }

    @Override
    public <T extends Event> EventServiceSubscriptionBuilder<T> request(final Class<T> eventClass) {
        return new DefaultEventServiceSubscriptionBuilder<>(this, eventClass);
    }

    @Override
    public <T extends Event> void subscribe(final LogSource source, final Class<T> eventClass, final NonProfileEventHandler<T> handler,
                                            final EventPriority priority, final boolean ignoreCancelled) throws QuestException {
        if (!eventService.require(eventClass, priority)) {
            throw new QuestException("<%s> Could not subscribe to event '%s'".formatted(source.getSourcePath(), eventClass.getSimpleName()));
        }
        final EventServiceSubscriber<T> subscriber = subNonProfile(handler);
        eventService.subscribe(eventClass, priority, ignoreCancelled, exceptionHandled(source, eventClass, subscriber));
        logger.debug(source, "Subscribed to event '" + eventClass.getSimpleName() + "' with priority '" + priority.name() + "' and ignoreCancelled '" + ignoreCancelled + "'");
    }

    @Override
    public <T extends Event> void subscribe(final LogSource source, final Class<T> eventClass, final ProfileEventHandler<T> handler,
                                            final QuestFunction<T, Optional<UUID>> playerUUIDExtractor,
                                            final EventPriority priority, final boolean ignoreCancelled) throws QuestException {
        if (!eventService.require(eventClass, priority)) {
            throw new QuestException("<%s> Could not subscribe to event '%s'".formatted(source.getSourcePath(), eventClass.getSimpleName()));
        }
        final EventServiceSubscriber<T> subscriber = subOffline(handler, playerUUIDExtractor);
        eventService.subscribe(eventClass, priority, ignoreCancelled, exceptionHandled(source, eventClass, subscriber));
        logger.debug(source, "Subscribed to event '" + eventClass.getSimpleName() + "' with priority '" + priority.name() + "' and ignoreCancelled '" + ignoreCancelled + "'");
    }

    @Override
    public <T extends Event> void subscribe(final LogSource source, final Class<T> eventClass,
                                            final OnlineProfileEventHandler<T> handler,
                                            final QuestFunction<T, Optional<UUID>> playerUUIDExtractor,
                                            final EventPriority priority, final boolean ignoreCancelled) throws QuestException {
        if (!eventService.require(eventClass, priority)) {
            throw new QuestException("<%s> Could not subscribe to event '%s'".formatted(source.getSourcePath(), eventClass.getSimpleName()));
        }
        final EventServiceSubscriber<T> subscriber = subOline(handler, playerUUIDExtractor);
        eventService.subscribe(eventClass, priority, ignoreCancelled, exceptionHandled(source, eventClass, subscriber));
        logger.debug(source, "Subscribed to event '" + eventClass.getSimpleName() + "' with priority '" + priority.name() + "' and ignoreCancelled '" + ignoreCancelled + "'");
    }

    private <T extends Event> EventServiceSubscriber<T> exceptionHandled(final LogSource source, final Class<T> eventClass,
                                                                         final EventServiceSubscriber<T> subscriber) {
        final QuestExceptionHandler exceptionHandler = new QuestExceptionHandler(source, logger, eventClass.getSimpleName());
        return (event, priority) -> exceptionHandler.handle(() -> subscriber.call(event, priority));
    }

    private <T extends Event> EventServiceSubscriber<T> subNonProfile(final NonProfileEventHandler<T> eventHandler) {
        return (event, priority) -> eventHandler.handle(event);
    }

    private <T extends Event> EventServiceSubscriber<T> subOline(final OnlineProfileEventHandler<T> handler,
                                                                 final QuestFunction<T, Optional<UUID>> playerUUIDExtractor) {
        return (event, prio) -> {
            final Optional<Profile> profile = playerUUIDExtractor.apply(event).map(profileProvider::getProfile);
            if (profile.isEmpty()) {
                return;
            }
            final Optional<OnlineProfile> onlineProfile = profile.get().getOnlineProfile();
            if (onlineProfile.isEmpty()) {
                return;
            }
            handler.handle(event, onlineProfile.get());
        };
    }

    private <T extends Event> EventServiceSubscriber<T> subOffline(final ProfileEventHandler<T> handler,
                                                                   final QuestFunction<T, Optional<UUID>> playerUUIDExtractor) {
        return (event, prio) -> {
            final Optional<Profile> profile = playerUUIDExtractor.apply(event).map(profileProvider::getProfile);
            if (profile.isPresent()) {
                handler.handle(event, profile.get());
                return;
            }
            logger.warn("Could not find profile for event " + event.getClass().getSimpleName());
        };
    }
}
