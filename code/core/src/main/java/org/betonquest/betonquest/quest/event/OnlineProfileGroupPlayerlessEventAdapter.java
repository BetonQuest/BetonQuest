package org.betonquest.betonquest.quest.event;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerlessAction;
import org.bukkit.entity.Player;

import java.util.function.Supplier;

/**
 * Adapt a player event as a playerless event by applying it to a group of online {@link Player}s. The group supplying
 * function will be called every time the event is executed.
 */
public class OnlineProfileGroupPlayerlessEventAdapter implements PlayerlessAction {

    /**
     * The supplier for generating the group of online players to use.
     */
    private final Supplier<? extends Iterable<? extends OnlineProfile>> profileCollectionSupplier;

    /**
     * The event to execute for every player of the group.
     */
    private final PlayerAction playerEvent;

    /**
     * Create a playerless event that will execute a normal event for every player provided by the supplying function.
     *
     * @param profileSupplier supplier for the player group
     * @param playerEvent     event to execute
     */
    public OnlineProfileGroupPlayerlessEventAdapter(final Supplier<? extends Iterable<? extends OnlineProfile>> profileSupplier, final PlayerAction playerEvent) {
        profileCollectionSupplier = profileSupplier;
        this.playerEvent = playerEvent;
    }

    @Override
    public void execute() throws QuestException {
        for (final OnlineProfile onlineProfile : profileCollectionSupplier.get()) {
            playerEvent.execute(onlineProfile);
        }
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return playerEvent.isPrimaryThreadEnforced();
    }
}
