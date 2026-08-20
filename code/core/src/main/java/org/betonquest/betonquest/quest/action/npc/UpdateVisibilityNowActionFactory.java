package org.betonquest.betonquest.quest.action.npc;

import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.action.OnlineActionAdapter;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.api.quest.npc.DefaultNpcHider;
import org.betonquest.betonquest.playerhider.PlayerHider;

/**
 * Factory to create {@link UpdateVisibilityNowAction}s from {@link Instruction}s.
 */
public class UpdateVisibilityNowActionFactory implements PlayerActionFactory {

    /**
     * Npc Hider to update visibility.
     */
    private final DefaultNpcHider npcHider;

    /**
     * Player Hider to update visibility.
     */
    private final PlayerHider playerHider;

    /**
     * Create the Npc visibility update action factory.
     *
     * @param npcHider    the hider where to update the visibility for npc
     * @param playerHider the hider where to update the visibility for player
     */
    public UpdateVisibilityNowActionFactory(final DefaultNpcHider npcHider, final PlayerHider playerHider) {
        this.npcHider = npcHider;
        this.playerHider = playerHider;
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) {
        return new OnlineActionAdapter(new UpdateVisibilityNowAction(npcHider, playerHider));
    }
}
