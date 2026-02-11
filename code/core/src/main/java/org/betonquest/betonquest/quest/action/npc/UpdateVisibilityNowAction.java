package org.betonquest.betonquest.quest.action.npc;

import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.action.OnlineAction;
import org.betonquest.betonquest.api.quest.npc.DefaultNpcHider;

/**
 * Action to update the visibility of all Npcs to one player now.
 */
public class UpdateVisibilityNowAction implements OnlineAction {

    /**
     * Npc Hider to update the visibility.
     */
    private final DefaultNpcHider npcHider;

    /**
     * Create a new update visibility action.
     *
     * @param npcHider the hider to update the visibility
     */
    public UpdateVisibilityNowAction(final DefaultNpcHider npcHider) {
        this.npcHider = npcHider;
    }

    @Override
    public void execute(final OnlineProfile profile) {
        npcHider.applyVisibility(profile);
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
