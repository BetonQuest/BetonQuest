package org.betonquest.betonquest.compatibility.npc.citizens.event.move;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.feature.FeatureApi;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.PlayerlessEvent;
import org.betonquest.betonquest.api.quest.event.PlayerlessEventFactory;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEventAdapter;
import org.betonquest.betonquest.api.quest.npc.NpcID;
import org.betonquest.betonquest.compatibility.npc.citizens.CitizensArgument;

/**
 * Factory for {@link CitizensStopEvent} from the {@link Instruction}.
 */
public class CitizensStopEventFactory implements PlayerlessEventFactory, PlayerEventFactory {

    /**
     * Feature API.
     */
    private final FeatureApi featureApi;

    /**
     * Move Controller where to stop the NPC movement.
     */
    private final CitizensMoveController citizensMoveController;

    /**
     * Create a new NPCTeleportEventFactory.
     *
     * @param featureApi             the Feature API
     * @param citizensMoveController the move controller where to stop the NPC movement
     */
    public CitizensStopEventFactory(final FeatureApi featureApi, final CitizensMoveController citizensMoveController) {
        this.featureApi = featureApi;
        this.citizensMoveController = citizensMoveController;
    }

    @Override
    public PlayerlessEvent parsePlayerless(final Instruction instruction) throws QuestException {
        return createCitizensStopEvent(instruction);
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        return createCitizensStopEvent(instruction);
    }

    private NullableEventAdapter createCitizensStopEvent(final Instruction instruction) throws QuestException {
        final Argument<NpcID> npcId = instruction.parse(CitizensArgument.CITIZENS_ID).get();
        return new NullableEventAdapter(new CitizensStopEvent(featureApi, npcId, citizensMoveController));
    }
}
