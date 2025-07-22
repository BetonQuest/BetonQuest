package org.betonquest.betonquest.compatibility.npc.citizens.event.move;

import org.betonquest.betonquest.api.feature.FeatureAPI;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.PlayerlessEvent;
import org.betonquest.betonquest.api.quest.event.PlayerlessEventFactory;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEventAdapter;
import org.betonquest.betonquest.compatibility.npc.citizens.CitizensArgument;
import org.betonquest.betonquest.id.NpcID;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadEvent;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadPlayerlessEvent;

/**
 * Factory for {@link CitizensStopEvent} from the {@link Instruction}.
 */
public class CitizensStopEventFactory implements PlayerlessEventFactory, PlayerEventFactory {

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
        return new PrimaryServerThreadPlayerlessEvent(createCitizensStopEvent(instruction), primaryServerThreadData);
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        return new PrimaryServerThreadEvent(createCitizensStopEvent(instruction), primaryServerThreadData);
    }

    private NullableEventAdapter createCitizensStopEvent(final Instruction instruction) throws QuestException {
        final Variable<NpcID> npcId = instruction.get(CitizensArgument.CITIZENS_ID);
        return new NullableEventAdapter(new CitizensStopEvent(featureAPI, npcId, citizensMoveController));
    }
}
