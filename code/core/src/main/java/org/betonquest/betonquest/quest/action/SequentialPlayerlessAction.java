package org.betonquest.betonquest.quest.action;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.quest.PrimaryThreadEnforceable;
import org.betonquest.betonquest.api.quest.action.PlayerlessAction;

import java.util.List;

/**
 * A static action that is composed of other static actions executed in sequence. If an error occurs execution is stopped
 * at that point.
 */
public class SequentialPlayerlessAction implements PlayerlessAction {

    /**
     * Actions to be executed.
     */
    private final List<PlayerlessAction> playerlessActions;

    /**
     * Create a static action sequence. The actions at the front of the array will be executed first, at the end will be
     * executed last.
     *
     * @param playerlessActions actions to be executed
     */
    public SequentialPlayerlessAction(final List<PlayerlessAction> playerlessActions) {
        this.playerlessActions = playerlessActions;
    }

    @Override
    public void execute() throws QuestException {
        for (final PlayerlessAction playerlessAction : playerlessActions) {
            playerlessAction.execute();
        }
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return playerlessActions.stream().anyMatch(PrimaryThreadEnforceable::isPrimaryThreadEnforced);
    }
}
