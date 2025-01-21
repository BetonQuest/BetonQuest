package org.betonquest.betonquest.compatibility.citizens.event.move;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.StaticEvent;
import org.betonquest.betonquest.api.quest.event.StaticEventFactory;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadStaticEvent;

/**
 * Factory for {@link CitizensStopEvent} from the {@link Instruction}.
 */
public class CitizensStopEventFactory implements StaticEventFactory {
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
    public StaticEvent parseStaticEvent(final Instruction instruction) throws QuestException {
        final int npcId = instruction.getInt();
        return new PrimaryServerThreadStaticEvent(new CitizensStopEvent(npcId, citizensMoveController), primaryServerThreadData);
    }
}
