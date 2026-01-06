package org.betonquest.betonquest.api.quest.objective.event;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.logger.LogSource;
import org.betonquest.betonquest.api.profile.Profile;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
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
     * A {@link NonProfileEventHandler} does not provide any profile information
     * and therefore offering profile-specific functionality.
     *
     * @param handler the handler to use
     * @return this
     */
    @Contract("_ -> this")
    EventServiceSubscriptionBuilder<T> handler(NonProfileEventHandler<T> handler);

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
    EventServiceSubscriptionBuilder<T> handler(ProfileEventHandler<T> handler, OfflinePlayerExtractor<T> playerExtractor);

    /**
     * Required build call. Sets the profile handler to be called by the bukkit event.
     * A {@link ProfileEventHandler} provides the profile information for the event retrieved from the extractor.
     *
     * @param handler         the handler to use
     * @param entityExtractor a method to extract the player from the event carrying only an entity
     *                        to retrieve the profile
     * @return this
     */
    @Contract("_, _ -> this")
    EventServiceSubscriptionBuilder<T> handler(ProfileEventHandler<T> handler, EntityExtractor<T> entityExtractor);

    /**
     * Required build call. Sets the profile handler to be called by the bukkit event.
     * A {@link ProfileEventHandler} provides the profile information for the event retrieved from the extractor.
     *
     * @param handler          the handler to use
     * @param profileExtractor a method to extract the profile directly from the event
     * @return this
     */
    @Contract("_, _ -> this")
    EventServiceSubscriptionBuilder<T> handler(ProfileEventHandler<T> handler, ProfileExtractor<T> profileExtractor);

    /**
     * Required build call. Sets the profile handler to be called by the bukkit event.
     * A {@link OnlineProfileEventHandler} provides the profile information for the event retrieved from the extractor.
     *
     * @param handler         the handler to use
     * @param entityExtractor a method to extract the player from the event carrying only an entity
     *                        to retrieve the profile
     * @return this
     */
    @Contract("_, _ -> this")
    EventServiceSubscriptionBuilder<T> handler(OnlineProfileEventHandler<T> handler, EntityExtractor<T> entityExtractor);

    /**
     * Required last build call. Registers the subscription with the {@link ObjectiveService}.
     *
     * @param ignoreCancelled if canceled events should be ignored
     * @throws QuestException if the subscription could not be registered
     */
    void subscribe(boolean ignoreCancelled) throws QuestException;

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
        UUID readUUID(T event);
    }

    /**
     * Extracts the player from the event.
     *
     * @param <T> the event type
     */
    @FunctionalInterface
    interface OfflinePlayerExtractor<T extends Event> {

        /**
         * Extracts the player from the event.
         *
         * @param event the event to extract from
         * @return the player of the event
         */
        @Nullable
        OfflinePlayer readPlayer(T event);
    }

    /**
     * Extracts the player from the event, that is available for all entities.
     * Assumes that only valid players shall be considered for this event.
     *
     * @param <T> the event type
     */
    @FunctionalInterface
    interface EntityExtractor<T extends Event> {

        /**
         * Extracts the player from the event.
         *
         * @param event the event to extract from
         * @return the player of the event
         */
        @Nullable
        Entity readEntity(T event);
    }

    /**
     * Extracts the player from the event, that is available for all entities.
     * Assumes that only valid players shall be considered for this event.
     *
     * @param <T> the event type
     */
    @FunctionalInterface
    interface ProfileExtractor<T extends Event> {

        /**
         * Extracts the player from the event.
         *
         * @param event the event to extract from
         * @return the player of the event
         */
        @Nullable
        Profile readProfile(T event);
    }
}
