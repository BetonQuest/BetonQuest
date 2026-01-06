package org.betonquest.betonquest.quest.event;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.action.online.OnlineAction;
import org.bukkit.entity.Player;

/**
 * Adapt an event to be run as Op.
 * <p>
 * Gives the player op, executes the nested event and then reverts the operation if necessary.
 */
public class OpPlayerActionAdapter implements OnlineAction {

    /**
     * The event to execute as Op.
     */
    private final OnlineAction event;

    /**
     * Creates a new OpPlayerEventAdapter.
     *
     * @param event the event to execute as op.
     */
    public OpPlayerActionAdapter(final OnlineAction event) {
        this.event = event;
    }

    @Override
    public void execute(final OnlineProfile profile) throws QuestException {
        final Player player = profile.getPlayer();
        final boolean previousOp = player.isOp();
        try {
            player.setOp(true);
            event.execute(profile);
        } finally {
            player.setOp(previousOp);
        }
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return event.isPrimaryThreadEnforced();
    }
}
