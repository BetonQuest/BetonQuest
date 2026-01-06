package org.betonquest.betonquest.quest.action;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerlessAction;
import org.bukkit.entity.Player;

import java.util.function.Supplier;

/**
 * Adapt a player action as a playerless action by applying it to a group of online {@link Player}s. The group supplying
 * function will be called every time the action is executed.
 */
public class OnlineProfileGroupPlayerlessActionAdapter implements PlayerlessAction {

    /**
     * The supplier for generating the group of online players to use.
     */
    private final Supplier<? extends Iterable<? extends OnlineProfile>> profileCollectionSupplier;

    /**
     * The action to execute for every player of the group.
     */
    private final PlayerAction playerAction;

    /**
     * Create a playerless action that will execute a normal action for every player provided by the supplying function.
     *
     * @param profileSupplier supplier for the player group
     * @param playerAction    action to execute
     */
    public OnlineProfileGroupPlayerlessActionAdapter(final Supplier<? extends Iterable<? extends OnlineProfile>> profileSupplier, final PlayerAction playerAction) {
        profileCollectionSupplier = profileSupplier;
        this.playerAction = playerAction;
    }

    @Override
    public void execute() throws QuestException {
        for (final OnlineProfile onlineProfile : profileCollectionSupplier.get()) {
            playerAction.execute(onlineProfile);
        }
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return playerAction.isPrimaryThreadEnforced();
    }
}
