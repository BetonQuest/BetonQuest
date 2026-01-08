package org.betonquest.betonquest.api.quest.objective.event;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.bukkit.event.BukkitEventService;
import org.betonquest.betonquest.api.bukkit.event.EventServiceSubscriber;
import org.betonquest.betonquest.api.common.function.QuestBiFunction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.objective.ObjectiveID;
import org.betonquest.betonquest.kernel.processor.quest.ActionProcessor;
import org.betonquest.betonquest.kernel.processor.quest.ConditionProcessor;
import org.betonquest.betonquest.lib.bukkit.event.DefaultBukkitEventService;
import org.betonquest.betonquest.lib.logger.QuestExceptionHandler;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
     * The processors to process actions.
     */
    private final ActionProcessor actionProcessor;

    /**
     * The processors to process conditions.
     */
    private final ConditionProcessor conditionProcessor;

    /**
     * The logger for this service.
     */
    private final BetonQuestLogger logger;

    /**
     * The logger factory to inject into other services.
     */
    private final BetonQuestLoggerFactory factory;

    /**
     * The map holding the objectives service data.
     */
    private final Map<ObjectiveID, DefaultObjectiveFactoryService> services;

    /**
     * Sole constructor. Creates an objective event service on top of a {@link BukkitEventService}.
     *
     * @param plugin             the plugin instance
     * @param conditionProcessor the condition processor
     * @param actionProcessor    the action processor
     * @param factory            the logger factory
     * @param profileProvider    the profile provider
     */
    public DefaultObjectiveService(final Plugin plugin, final ConditionProcessor conditionProcessor, final ActionProcessor actionProcessor,
                                   final BetonQuestLoggerFactory factory, final ProfileProvider profileProvider) {
        this.eventService = new DefaultBukkitEventService(plugin, factory);
        this.factory = factory;
        this.logger = this.factory.create(DefaultObjectiveService.class);
        this.profileProvider = profileProvider;
        this.actionProcessor = actionProcessor;
        this.conditionProcessor = conditionProcessor;
        this.services = new HashMap<>();
    }

    @Override
    public void clear() {
        eventService.unsubscribeAll();
        services.clear();
    }

    @Override
    public ObjectiveFactoryService getFactoryService(final ObjectiveID objectiveID) throws QuestException {
        if (services.containsKey(objectiveID)) {
            return services.get(objectiveID);
        }
        final DefaultObjectiveFactoryService service = new DefaultObjectiveFactoryService(objectiveID,
                actionProcessor, conditionProcessor, this, factory, profileProvider);
        services.put(objectiveID, service);
        return service;
    }

    @Override
    public <T extends Event> EventServiceSubscriptionBuilder<T> request(final Class<T> eventClass) {
        return new DefaultEventServiceSubscriptionBuilder<>(this, eventClass);
    }

    @Override
    public <T extends Event> void subscribe(final ObjectiveID objectiveID, final Class<T> eventClass, final NonProfileEventHandler<T> handler,
                                            final EventPriority priority, final boolean ignoreCancelled) throws QuestException {
        if (!eventService.require(eventClass, priority)) {
            throw new QuestException("<%s> Could not subscribe to event '%s'".formatted(objectiveID, eventClass.getSimpleName()));
        }
        final EventServiceSubscriber<T> subscriber = subNonProfile(objectiveID, handler);
        final EventServiceSubscriber<T> exceptionHandled = exceptionHandled(objectiveID, eventClass, subscriber);
        eventService.subscribe(eventClass, priority, ignoreCancelled, exceptionHandled);
        logger.debug(objectiveID.getPackage(), "Subscribed to event '" + eventClass.getSimpleName() + "' with priority '" + priority.name() + "' and ignoreCancelled '" + ignoreCancelled + "'");
    }

    @Override
    public <T extends Event> void subscribe(final ObjectiveID objectiveID, final Class<T> eventClass, final ProfileEventHandler<T> handler,
                                            final QuestBiFunction<ProfileProvider, T, Optional<Profile>> profileExtractor,
                                            final EventPriority priority, final boolean ignoreCancelled) throws QuestException {
        if (!eventService.require(eventClass, priority)) {
            throw new QuestException("<%s> Could not subscribe to event '%s'".formatted(objectiveID.getFull(), eventClass.getSimpleName()));
        }
        final EventServiceSubscriber<T> subscriber = subOffline(objectiveID, handler, profileExtractor);
        final EventServiceSubscriber<T> exceptionHandled = exceptionHandled(objectiveID, eventClass, subscriber);
        eventService.subscribe(eventClass, priority, ignoreCancelled, exceptionHandled);
        logger.debug(objectiveID.getPackage(), "Subscribed to event '" + eventClass.getSimpleName() + "' with priority '" + priority.name() + "' and ignoreCancelled '" + ignoreCancelled + "'");
    }

    @Override
    public <T extends Event> void subscribe(final ObjectiveID objectiveID, final Class<T> eventClass,
                                            final OnlineProfileEventHandler<T> handler,
                                            final QuestBiFunction<ProfileProvider, T, Optional<Profile>> profileExtractor,
                                            final EventPriority priority, final boolean ignoreCancelled) throws QuestException {
        if (!eventService.require(eventClass, priority)) {
            throw new QuestException("<%s> Could not subscribe to event '%s'".formatted(objectiveID.getFull(), eventClass.getSimpleName()));
        }
        final EventServiceSubscriber<T> subscriber = subOnline(objectiveID, handler, profileExtractor);
        final EventServiceSubscriber<T> exceptionHandled = exceptionHandled(objectiveID, eventClass, subscriber);
        eventService.subscribe(eventClass, priority, ignoreCancelled, exceptionHandled);
        logger.debug(objectiveID.getPackage(), "Subscribed to event '" + eventClass.getSimpleName() + "' with priority '" + priority.name() + "' and ignoreCancelled '" + ignoreCancelled + "'");
    }

    private <T extends Event> EventServiceSubscriber<T> exceptionHandled(final ObjectiveID objectiveID, final Class<T> eventClass,
                                                                         final EventServiceSubscriber<T> subscriber) {
        final QuestExceptionHandler exceptionHandler = new QuestExceptionHandler(objectiveID.getPackage(), logger, objectiveID.getFull(), eventClass.getSimpleName());
        return (event, priority) -> exceptionHandler.handle(() -> subscriber.call(event, priority));
    }

    private <T extends Event> EventServiceSubscriber<T> subNonProfile(final ObjectiveID objectiveID, final NonProfileEventHandler<T> eventHandler) {
        return (event, priority) -> {
            final ObjectiveFactoryService service = getFactoryService(objectiveID);
            if (service.checkConditions(null)) {
                eventHandler.handle(event);
            }
        };
    }

    private <T extends Event> EventServiceSubscriber<T> subOnline(final ObjectiveID objectiveID, final OnlineProfileEventHandler<T> handler,
                                                                  final QuestBiFunction<ProfileProvider, T, Optional<Profile>> profileExtractor) {
        return (event, prio) -> {
            final Optional<Profile> profile = profileExtractor.apply(profileProvider, event);
            if (profile.isEmpty()) {
                return;
            }
            final Optional<OnlineProfile> onlineProfile = profile.get().getOnlineProfile();
            if (onlineProfile.isEmpty()) {
                return;
            }
            final ObjectiveFactoryService service = getFactoryService(objectiveID);
            final OnlineProfile executingProfile = onlineProfile.get();
            if (service.containsProfile(executingProfile) && service.checkConditions(executingProfile)) {
                handler.handle(event, executingProfile);
            }
        };
    }

    private <T extends Event> EventServiceSubscriber<T> subOffline(final ObjectiveID objectiveID, final ProfileEventHandler<T> handler,
                                                                   final QuestBiFunction<ProfileProvider, T, Optional<Profile>> profileExtractor) {
        return (event, prio) -> {
            final Optional<Profile> profile = profileExtractor.apply(profileProvider, event);
            if (profile.isEmpty()) {
                return;
            }
            final ObjectiveFactoryService service = getFactoryService(objectiveID);
            final Profile executingProfile = profile.get();
            if (service.containsProfile(executingProfile) && service.checkConditions(executingProfile)) {
                handler.handle(event, executingProfile);
            }
        };
    }
}
