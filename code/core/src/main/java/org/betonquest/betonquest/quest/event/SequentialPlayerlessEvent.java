package org.betonquest.betonquest.quest.event;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.quest.PrimaryThreadEnforceable;
import org.betonquest.betonquest.api.quest.action.PlayerlessAction;

import java.util.List;

/**
 * A static event that is composed of other static events executed in sequence. If an error occurs execution is stopped
 * at that point.
 */
public class SequentialPlayerlessEvent implements PlayerlessAction {

    /**
     * Events to be executed.
     */
    private final List<PlayerlessAction> playerlessEvents;

    /**
     * Create a static event sequence. The events at the front of the array will be executed first, at the end will be
     * executed last.
     *
     * @param playerlessEvents events to be executed
     */
    public SequentialPlayerlessEvent(final List<PlayerlessAction> playerlessEvents) {
        this.playerlessEvents = playerlessEvents;
    }

    @Override
    public void execute() throws QuestException {
        for (final PlayerlessAction playerlessEvent : playerlessEvents) {
            playerlessEvent.execute();
        }
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return playerlessEvents.stream().anyMatch(PrimaryThreadEnforceable::isPrimaryThreadEnforced);
    }
}
