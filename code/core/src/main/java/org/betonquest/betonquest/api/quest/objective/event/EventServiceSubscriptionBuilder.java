package org.betonquest.betonquest.api.quest.objective.event;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.function.QuestFunction;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.Contract;

import java.util.Optional;
import java.util.UUID;

/**
 * The {@link EventServiceSubscriptionBuilder} allows creating a subscription with a builder pattern
 * as well as registering it with the {@link ObjectiveEventService}.
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
     * Required last build call. Registers the subscription with the {@link ObjectiveEventService}.
     *
     * @param ignoreCancelled if canceled events should be ignored
     */
    void subscribe(boolean ignoreCancelled);

    /**
     * Required last build call. Registers the subscription with the {@link ObjectiveEventService}.
     */
    void subscribe();

    /**
     * Extracts the player uuid from the event.
     *
     * @param <T> the event type
     */
    @FunctionalInterface
    interface PlayerUUIDExtractor<T extends Event> extends QuestFunction<T, UUID> {

    }

    /**
     * Extracts the player from the event.
     *
     * @param <T> the event type
     */
    @FunctionalInterface
    interface PlayerExtractor<T extends Event> extends QuestFunction<T, Optional<UUID>> {

        /**
         * Extracts the player from the event.
         *
         * @param event the event to extract from
         * @return the player of the event
         */
        Optional<Player> read(T event);

        @Override
        default Optional<UUID> apply(final T arg) throws QuestException {
            return read(arg).map(Player::getUniqueId);
        }
    }
}
