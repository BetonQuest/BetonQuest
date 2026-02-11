package org.betonquest.betonquest.quest.action.npc;

import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.action.OnlineActionAdapter;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.api.quest.npc.DefaultNpcHider;

/**
 * Factory to create {@link UpdateVisibilityNowAction}s from {@link Instruction}s.
 */
public class UpdateVisibilityNowActionFactory implements PlayerActionFactory {

    /**
     * Hider to update visibility.
     */
    private final DefaultNpcHider npcHider;

    /**
     * Logger factory to create a logger for the actions.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Create the Npc visibility update action factory.
     *
     * @param npcHider      the hider where to update the visibility
     * @param loggerFactory the logger factory to create a logger for the actions
     */
    public UpdateVisibilityNowActionFactory(final DefaultNpcHider npcHider, final BetonQuestLoggerFactory loggerFactory) {
        this.npcHider = npcHider;
        this.loggerFactory = loggerFactory;
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) {
        return new OnlineActionAdapter(new UpdateVisibilityNowAction(npcHider),
                loggerFactory.create(UpdateVisibilityNowAction.class), instruction.getPackage());
    }
}
