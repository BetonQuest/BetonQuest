package org.betonquest.betonquest.quest.action;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.action.online.OnlineAction;
import org.bukkit.entity.Player;

/**
 * Adapt an action to be run as Op.
 * <p>
 * Gives the player op, executes the nested action and then reverts the operation if necessary.
 */
public class OpPlayerActionAdapter implements OnlineAction {

    /**
     * The action to execute as Op.
     */
    private final OnlineAction action;

    /**
     * Creates a new OpPlayerActionAdapter.
     *
     * @param action the action to execute as op.
     */
    public OpPlayerActionAdapter(final OnlineAction action) {
        this.action = action;
    }

    @Override
    public void execute(final OnlineProfile profile) throws QuestException {
        final Player player = profile.getPlayer();
        final boolean previousOp = player.isOp();
        try {
            player.setOp(true);
            action.execute(profile);
        } finally {
            player.setOp(previousOp);
        }
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return action.isPrimaryThreadEnforced();
    }
}
