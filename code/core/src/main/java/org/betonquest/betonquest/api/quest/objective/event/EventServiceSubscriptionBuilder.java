package org.betonquest.betonquest.api.quest.objective.event;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.logger.LogSource;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * The {@link EventServiceSubscriptionBuilder} allows creating a subscription with a builder pattern
 * as well as registering it with the {@link ObjectiveService}.
 *
 * @param <T> the event type
 */
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
     * Optional build call. Defaults to {@link LogSource#EMPTY}.
     *
     * @param source the source to be used for logging
     * @return this
     */
    @Contract("_ -> this")
    EventServiceSubscriptionBuilder<T> source(LogSource source);

    /**
     * Required build call. Sets the static handler to be called by the bukkit event.
     * A {@link StaticEventHandler} does not provide any profile information
     * and therefore offering profile-specific functionality.
     *
     * @param handler the handler to use
     * @return this
     */
    @Contract("_ -> this")
    EventServiceSubscriptionBuilder<T> handler(StaticEventHandler<T> handler);

    /**
     * Required build call. Sets the profile handler to be called by the bukkit event.
     * A {@link ProfileEventHandler} provides the profile information for the event retrieved from the extractor.
     *
     * @param handler         the handler to use
     * @param playerExtractor a method to extract the player uuid from the event to retrieve the profile
     * @return this
     */
    @Contract("_, _ -> this")
    EventServiceSubscriptionBuilder<T> handler(ProfileEventHandler<T> handler, PlayerUUIDExtractor<T> playerExtractor);

    /**
     * Required build call. Sets the profile handler to be called by the bukkit event.
     * A {@link ProfileEventHandler} provides the profile information for the event retrieved from the extractor.
     *
     * @param handler         the handler to use
     * @param playerExtractor a method to extract the player from the event to retrieve the profile
     * @return this
     */
    @Contract("_, _ -> this")
    EventServiceSubscriptionBuilder<T> handler(ProfileEventHandler<T> handler, PlayerExtractor<T> playerExtractor);

    /**
     * Required build call. Sets the profile handler to be called by the bukkit event.
     * A {@link OnlineProfileEventHandler} provides the profile information for the event retrieved from the extractor.
     *
     * @param handler         the handler to use
     * @param playerExtractor a method to extract the player from the event to retrieve the profile
     * @return this
     */
    @Contract("_, _ -> this")
    EventServiceSubscriptionBuilder<T> handler(OnlineProfileEventHandler<T> handler, PlayerExtractor<T> playerExtractor);

    /**
     * Required last build call. Registers the subscription with the {@link ObjectiveService}.
     *
     * @param ignoreCancelled if canceled events should be ignored
     * @throws QuestException if the subscription could not be registered
     */
    void subscribe(boolean ignoreCancelled) throws QuestException;

    /**
     * Required last build call. Registers the subscription with the {@link ObjectiveService}.
     *
     * @throws QuestException if the subscription could not be registered
     */
    void subscribe() throws QuestException;

    /**
     * Extracts the player uuid from the event.
     *
     * @param <T> the event type
     */
    @FunctionalInterface
    interface PlayerUUIDExtractor<T extends Event> {

        /**
         * Extracts the player uuid from the event.
         *
         * @param event the event to extract from
         * @return the player uuid of the event
         */
        @Nullable
        UUID read(T event);
    }

    /**
     * Extracts the player from the event.
     *
     * @param <T> the event type
     */
    @FunctionalInterface
    interface PlayerExtractor<T extends Event> {

        /**
         * Extracts the player from the event.
         *
         * @param event the event to extract from
         * @return the player of the event
         */
        @Nullable
        Player read(T event);
    }
}
