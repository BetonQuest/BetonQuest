package org.betonquest.betonquest.quest.event.kill;

import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.event.online.OnlineEvent;

/**
 * Kills the player.
 */
public class KillEvent implements OnlineEvent {

    /**
     * Creates a new kill event.
     */
    public KillEvent() {
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
