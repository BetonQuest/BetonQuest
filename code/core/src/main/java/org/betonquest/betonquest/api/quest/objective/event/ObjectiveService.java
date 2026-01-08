package org.betonquest.betonquest.api.quest.objective.event;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.function.QuestBiFunction;
import org.betonquest.betonquest.api.logger.LogSource;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.objective.ObjectiveID;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;

import java.util.Optional;

/**
 * The service for objectives managing the subscription of event handlers.
 */
public interface ObjectiveService {

    /**
     * Resets the entire service.
     * Use with caution!
     */
    void clear();

    /**
     * Creates a new {@link ObjectiveFactoryService} for the given objectiveId.
     *
     * @param objectiveID the objective to create a subscription service for
     * @return a new {@link ObjectiveFactoryService} for the given objectiveId
     * @throws QuestException if the objective causes issues with creating a factory service
     */
    ObjectiveFactoryService getFactoryService(ObjectiveID objectiveID) throws QuestException;

    /**
     * Requests a new event subscription using an {@link EventServiceSubscriptionBuilder}.
     * The request may be completed in one chain of calls requiring at least a handler and ending with
     * {@link EventServiceSubscriptionBuilder#subscribe(boolean)}.
     *
     * @param eventClass the event class to subscribe to
     * @param <T>        the event type
     * @return a new {@link EventServiceSubscriptionBuilder} for the requested event
     */
    <T extends Event> EventServiceSubscriptionBuilder<T> request(Class<T> eventClass);

    /**
     * Registers a new event subscription for a specific non-profile event.
     *
     * @param source          the source to use for logging
     * @param eventClass      the class of the event to subscribe to
     * @param handler         the handler to call when the event is triggered
     * @param priority        the priority of the event listener
     * @param ignoreCancelled if the event should be ignored if canceled
     * @param <T>             the event type
     * @throws QuestException if the event could not be subscribed
     */
    <T extends Event> void subscribe(LogSource source, Class<T> eventClass, NonProfileEventHandler<T> handler,
                                     EventPriority priority, boolean ignoreCancelled) throws QuestException;

    /**
     * Registers a new event subscription for a specific event with a profile involved.
     *
     * @param source           the source to use for logging
     * @param eventClass       the event class to subscribe to
     * @param handler          the handler to call when the event is triggered
     * @param profileExtractor a method to extract the profile from the event
     * @param priority         the priority of the event listener
     * @param ignoreCancelled  if the event should be ignored if canceled
     * @param <T>              the event type
     * @throws QuestException if the event could not be subscribed
     */
    <T extends Event> void subscribe(LogSource source, Class<T> eventClass, ProfileEventHandler<T> handler,
                                     QuestBiFunction<ProfileProvider, T, Optional<Profile>> profileExtractor,
                                     EventPriority priority, boolean ignoreCancelled) throws QuestException;

    /**
     * Registers a new event subscription for a specific event with a profile involved.
     *
     * @param source           the source to use for logging
     * @param eventClass       the event class to subscribe to
     * @param handler          the handler to call when the event is triggered
     * @param profileExtractor a method to extract the profile from the event
     * @param priority         the priority of the event listener
     * @param ignoreCancelled  if the event should be ignored if canceled
     * @param <T>              the event type
     * @throws QuestException if the event could not be subscribed
     */
    <T extends Event> void subscribe(LogSource source, Class<T> eventClass, OnlineProfileEventHandler<T> handler,
                                     QuestBiFunction<ProfileProvider, T, Optional<Profile>> profileExtractor,
                                     EventPriority priority, boolean ignoreCancelled) throws QuestException;
}
