package org.betonquest.betonquest.compatibility.npc.citizens.event.move;

import org.betonquest.betonquest.api.feature.FeatureAPI;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.PlayerlessEvent;
import org.betonquest.betonquest.api.quest.event.PlayerlessEventFactory;
import org.betonquest.betonquest.id.NpcID;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadPlayerlessEvent;

/**
 * Factory for {@link CitizensStopEvent} from the {@link Instruction}.
 */
public class CitizensStopEventFactory implements PlayerlessEventFactory {

    /**
     * Feature API.
     */
    private final FeatureAPI featureAPI;

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
     * @param featureAPI              the Feature API
     * @param primaryServerThreadData the data to use for syncing to the primary server thread
     * @param citizensMoveController  the move controller where to stop the NPC movement
     */
    public CitizensStopEventFactory(final FeatureAPI featureAPI, final PrimaryServerThreadData primaryServerThreadData, final CitizensMoveController citizensMoveController) {
        this.featureAPI = featureAPI;
        this.primaryServerThreadData = primaryServerThreadData;
        this.citizensMoveController = citizensMoveController;
    }

    @Override
    public PlayerlessEvent parsePlayerless(final Instruction instruction) throws QuestException {
        final NpcID npcId = instruction.getID(NpcID::new);
        final Instruction npcInstruction = npcId.getInstruction();
        if (!"citizens".equals(npcInstruction.getPart(0))) {
            throw new QuestException("Cannot use non-Citizens NPC ID!");
        }
        return new PrimaryServerThreadPlayerlessEvent(new CitizensStopEvent(featureAPI, npcId, citizensMoveController), primaryServerThreadData);
    }
}
