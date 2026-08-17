package org.betonquest.betonquest.quest.action.npc;

import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.action.OnlineAction;
import org.betonquest.betonquest.api.quest.npc.DefaultNpcHider;
import org.betonquest.betonquest.playerhider.PlayerHider;

/**
 * Action to update the visibility of all Npcs to one player now.
 */
public class UpdateVisibilityNowAction implements OnlineAction {

    /**
     * Npc Hider to update the visibility.
     */
    private final DefaultNpcHider npcHider;

    /**
     * Player Hider to update the visibility.
     */
    private final PlayerHider playerHider;

    /**
     * Create a new update visibility action.
     *
     * @param npcHider    the hider to update the visibility for npc
     * @param playerHider the hider to update the visibility for player
     */
    public UpdateVisibilityNowAction(final DefaultNpcHider npcHider, final PlayerHider playerHider) {
        this.npcHider = npcHider;
        this.playerHider = playerHider;
    }

    @Override
    public void execute(final OnlineProfile profile) {
        npcHider.applyVisibility(profile);
        playerHider.updateVisibility(profile);
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
