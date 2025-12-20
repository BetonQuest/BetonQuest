package org.betonquest.betonquest.compatibility.npc.citizens.event.move;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.feature.FeatureApi;
import org.betonquest.betonquest.api.instruction.DefaultInstruction;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.PlayerlessEvent;
import org.betonquest.betonquest.api.quest.event.PlayerlessEventFactory;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEventAdapter;
import org.betonquest.betonquest.api.quest.event.thread.PrimaryServerThreadEvent;
import org.betonquest.betonquest.api.quest.event.thread.PrimaryServerThreadPlayerlessEvent;
import org.betonquest.betonquest.api.quest.npc.NpcID;
import org.betonquest.betonquest.compatibility.npc.citizens.CitizensArgument;

/**
 * Factory for {@link CitizensStopEvent} from the {@link DefaultInstruction}.
 */
public class CitizensStopEventFactory implements PlayerlessEventFactory, PlayerEventFactory {

    /**
     * Feature API.
     */
    private final FeatureApi featureApi;

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
     * @param featureApi              the Feature API
     * @param primaryServerThreadData the data to use for syncing to the primary server thread
     * @param citizensMoveController  the move controller where to stop the NPC movement
     */
    public CitizensStopEventFactory(final FeatureApi featureApi, final PrimaryServerThreadData primaryServerThreadData, final CitizensMoveController citizensMoveController) {
        this.featureApi = featureApi;
        this.primaryServerThreadData = primaryServerThreadData;
        this.citizensMoveController = citizensMoveController;
    }

    @Override
    public PlayerlessEvent parsePlayerless(final DefaultInstruction instruction) throws QuestException {
        return new PrimaryServerThreadPlayerlessEvent(createCitizensStopEvent(instruction), primaryServerThreadData);
    }

    @Override
    public PlayerEvent parsePlayer(final DefaultInstruction instruction) throws QuestException {
        return new PrimaryServerThreadEvent(createCitizensStopEvent(instruction), primaryServerThreadData);
    }

    private NullableEventAdapter createCitizensStopEvent(final DefaultInstruction instruction) throws QuestException {
        final Variable<NpcID> npcId = instruction.get(CitizensArgument.CITIZENS_ID);
        return new NullableEventAdapter(new CitizensStopEvent(featureApi, npcId, citizensMoveController));
    }
}
