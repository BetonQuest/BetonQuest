package org.betonquest.betonquest.quest.event;

import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.StaticEvent;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.entity.Player;

import java.util.function.Supplier;

/**
 * Adapt a normal event as a "static" event by applying it to a group of online {@link Player}s. The group supplying
 * function will be called every time the event is executed.
 */
public class OnlinePlayerGroupStaticEventAdapter implements StaticEvent {
    /**
     * The supplier for generating the group of online players to use.
     */
    private final Supplier<? extends Iterable<? extends Player>> playerCollectionSupplier;

    /**
     * The event to execute for every player of the group.
     */
    private final Event event;

    /**
     * Create a "static" event that will execute a normal event for every player provided by the supplying function.
     *
     * @param playerSupplier supplier for the player group
     * @param event          event to execute
     */
    public OnlinePlayerGroupStaticEventAdapter(final Supplier<? extends Iterable<? extends Player>> playerSupplier, final Event event) {
        playerCollectionSupplier = playerSupplier;
        this.event = event;
    }

    @Override
    public void execute() throws QuestRuntimeException {
        for (final Player player : playerCollectionSupplier.get()) {
            final Profile profile = PlayerConverter.getID(player);
            event.execute(profile);
        }
    }
}
