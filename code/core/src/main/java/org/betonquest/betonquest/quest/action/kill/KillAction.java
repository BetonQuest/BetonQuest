package org.betonquest.betonquest.quest.action.kill;

import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.action.OnlineAction;

/**
 * Kills the player.
 */
public class KillAction implements OnlineAction {

    /**
     * Creates a new kill action.
     */
    public KillAction() {
    }

    @Override
    public void execute(final OnlineProfile profile) {
        profile.getPlayer().setHealth(0);
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
