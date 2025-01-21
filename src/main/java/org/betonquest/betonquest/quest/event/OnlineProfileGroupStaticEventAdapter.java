package org.betonquest.betonquest.quest.event;

import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.StaticEvent;
import org.bukkit.entity.Player;

import java.util.function.Supplier;

/**
 * Adapt a normal event as a "static" event by applying it to a group of online {@link Player}s. The group supplying
 * function will be called every time the event is executed.
 */
public class OnlineProfileGroupStaticEventAdapter implements StaticEvent {
    /**
     * The supplier for generating the group of online players to use.
     */
    private final Supplier<? extends Iterable<? extends OnlineProfile>> profileCollectionSupplier;

    /**
     * The event to execute for every player of the group.
     */
    private final Event event;

    /**
     * Create a "static" event that will execute a normal event for every player provided by the supplying function.
     *
     * @param profileSupplier supplier for the player group
     * @param event           event to execute
     */
    public OnlineProfileGroupStaticEventAdapter(final Supplier<? extends Iterable<? extends OnlineProfile>> profileSupplier, final Event event) {
        profileCollectionSupplier = profileSupplier;
        this.event = event;
    }

    @Override
    public void execute() throws QuestException {
        for (final OnlineProfile onlineProfile : profileCollectionSupplier.get()) {
            event.execute(onlineProfile);
        }
    }
}
