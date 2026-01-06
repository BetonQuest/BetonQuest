package org.betonquest.betonquest.quest.action.npc;

import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.api.quest.action.online.OnlineActionAdapter;
import org.betonquest.betonquest.api.quest.npc.feature.NpcHider;

/**
 * Factory to create {@link UpdateVisibilityNowAction}s from {@link Instruction}s.
 */
public class UpdateVisibilityNowActionFactory implements PlayerActionFactory {

    /**
     * Hider to update visibility.
     */
    private final NpcHider npcHider;

    /**
     * Logger factory to create a logger for the events.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Create the Npc visibility update event factory.
     *
     * @param npcHider      the hider where to update the visibility
     * @param loggerFactory the logger factory to create a logger for the events
     */
    public UpdateVisibilityNowActionFactory(final NpcHider npcHider, final BetonQuestLoggerFactory loggerFactory) {
        this.npcHider = npcHider;
        this.loggerFactory = loggerFactory;
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) {
        return new OnlineActionAdapter(new UpdateVisibilityNowAction(npcHider),
                loggerFactory.create(UpdateVisibilityNowAction.class), instruction.getPackage());
    }
}
