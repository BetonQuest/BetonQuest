package org.betonquest.betonquest.compatibility.citizens.event.move;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.PlayerlessEvent;
import org.betonquest.betonquest.api.quest.event.PlayerlessEventFactory;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadPlayerlessEvent;

/**
 * Factory for {@link CitizensStopEvent} from the {@link Instruction}.
 */
public class CitizensStopEventFactory implements PlayerlessEventFactory {
    /**
     * Required data for executing on the main thread.
     */
    private final PrimaryServerThreadData primaryServerThreadData;

    /**
     * Move Controller where to stop the NPC movement.
     */
    private final CitizensMoveController citizensMoveController;

    /**
     * Create a new NPCTeleportEventFactory.
     *
     * @param primaryServerThreadData the data to use for syncing to the primary server thread
     * @param citizensMoveController  the move controller where to stop the NPC movement
     */
    public CitizensStopEventFactory(final PrimaryServerThreadData primaryServerThreadData, final CitizensMoveController citizensMoveController) {
        this.primaryServerThreadData = primaryServerThreadData;
        this.citizensMoveController = citizensMoveController;
    }

    @Override
    public PlayerlessEvent parsePlayerless(final Instruction instruction) throws QuestException {
        final int npcId = instruction.getInt();
        return new PrimaryServerThreadPlayerlessEvent(new CitizensStopEvent(npcId, citizensMoveController), primaryServerThreadData);
    }
}
