package org.betonquest.betonquest.api.quest.objective.service;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.function.QuestFunction;
import org.betonquest.betonquest.api.identifier.ObjectiveIdentifier;
import org.betonquest.betonquest.api.profile.Profile;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.Contract;

import java.util.UUID;

/**
 * The {@link EventServiceSubscriptionBuilder} allows creating a subscription with a builder pattern
 * as well as registering it with the {@link ObjectiveServiceProvider}.
 *
 * @param <T> the event type
 */
@SuppressWarnings("PMD.TooManyMethods")
public interface EventServiceSubscriptionBuilder<T extends Event> {

    /**
     * Optional build call. Defaults to {@link EventPriority#NORMAL}.
     * <br>
     * Sets the priority to be used for the bukkit event.
     *
     * @param priority the priority to use
     * @return this
     */
    @Contract("_ -> this")
    EventServiceSubscriptionBuilder<T> priority(EventPriority priority);

    /**
     * Required build call. Sets the objective related to the registered event.
     *
     * @param objectiveID the objective id
     * @return this
     */
    @Contract("_ -> this")
    EventServiceSubscriptionBuilder<T> source(ObjectiveIdentifier objectiveID);

    /**
     * Required build call. Sets the non-profile handler to be called by the bukkit event.
     * A {@link NonProfileEventHandler} does not provide any profile information
     * and therefore offering profile-specific functionality.
     *
     * @param handler the handler to use
     * @return this
     * @throws IllegalStateException if another handler is already set
     */
    @Contract("_ -> this")
    EventServiceSubscriptionBuilder<T> handler(NonProfileEventHandler<T> handler);

    /**
     * Required build call. Sets the profile handler to be called by the bukkit event.
     * A {@link ProfileEventHandler} provides the profile information for the event retrieved from the extractor.
     *
     * @param handler the handler to use
     * @return this
     * @throws IllegalStateException if another handler is already set
     */
    @Contract("_ -> this")
    EventServiceSubscriptionBuilder<T> handler(ProfileEventHandler<T> handler);

    /**
     * Required build call. Sets the online profile handler to be called by the bukkit event.
     * A {@link OnlineProfileEventHandler} provides the profile information for the event retrieved from the extractor.
     *
     * @param handler the handler to use
     * @return this
     * @throws IllegalStateException if another handler is already set
     */
    @Contract("_ -> this")
    EventServiceSubscriptionBuilder<T> onlineHandler(OnlineProfileEventHandler<T> handler);

    /**
     * Sets the extractor to be used for extracting the player uuid from the event.
     * This call is not required if the {@link #handler(NonProfileEventHandler)} call is used.
     *
     * @param extractor the extractor to use
     * @return this
     * @throws IllegalStateException if another extractor is already set
     */
    @Contract("_ -> this")
    EventServiceSubscriptionBuilder<T> uuid(QuestFunction<T, UUID> extractor);

    /**
     * Sets the extractor to be used for extracting the player from the event.
     * This call is not required if the {@link #handler(NonProfileEventHandler)} call is used.
     *
     * @param extractor the extractor to use
     * @return this
     * @throws IllegalStateException if another extractor is already set
     */
    @Contract("_ -> this")
    EventServiceSubscriptionBuilder<T> offlinePlayer(QuestFunction<T, OfflinePlayer> extractor);

    /**
     * Sets the extractor to be used for extracting the player from the event.
     * This call is not required if the {@link #handler(NonProfileEventHandler)} call is used.
     *
     * @param extractor the extractor to use
     * @return this
     * @throws IllegalStateException if another extractor is already set
     */
    @Contract("_ -> this")
    EventServiceSubscriptionBuilder<T> player(QuestFunction<T, Player> extractor);

    /**
     * Sets the extractor to be used for extracting the player from the event.
     * This call is not required if the {@link #handler(NonProfileEventHandler)} call is used.
     *
     * @param extractor the extractor to use
     * @return this
     * @throws IllegalStateException if another extractor is already set
     */
    @Contract("_ -> this")
    EventServiceSubscriptionBuilder<T> entity(QuestFunction<T, Entity> extractor);

    /**
     * Sets the extractor to be used for extracting the player from the event.
     * This call is not required if the {@link #handler(NonProfileEventHandler)} call is used.
     *
     * @param extractor the extractor to use
     * @return this
     * @throws IllegalStateException if another extractor is already set
     */
    @Contract("_ -> this")
    EventServiceSubscriptionBuilder<T> profile(QuestFunction<T, Profile> extractor);

    /**
     * Optional build call.
     * <br>
     * Sets to ignore the conditions check of the event handler.
     * If used the requested event for this builder will no longer receive the otherwise forced
     * {@link ObjectiveService#checkConditions(Profile)} check.
     *
     * @return this
     */
    @Contract("-> this")
    EventServiceSubscriptionBuilder<T> ignoreConditions();

    /**
     * Required last build call. Registers the subscription with the {@link ObjectiveServiceProvider}.
     *
     * @param ignoreCancelled if canceled events should be ignored
     * @throws QuestException        if the subscription could not be registered
     * @throws IllegalStateException if no valid handler-extractor pair was set
     */
    void subscribe(boolean ignoreCancelled) throws QuestException;
}
