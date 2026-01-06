package org.betonquest.betonquest.compatibility.npc.citizens.action.move;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.feature.FeatureApi;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.api.quest.action.PlayerlessAction;
import org.betonquest.betonquest.api.quest.action.PlayerlessActionFactory;
import org.betonquest.betonquest.api.quest.action.nullable.NullableActionAdapter;
import org.betonquest.betonquest.api.quest.npc.NpcID;
import org.betonquest.betonquest.compatibility.npc.citizens.CitizensArgument;

/**
 * Factory for {@link CitizensStopAction} from the {@link Instruction}.
 */
public class CitizensStopActionFactory implements PlayerlessActionFactory, PlayerActionFactory {

    /**
     * Feature API.
     */
    private final FeatureApi featureApi;

    /**
     * Move Controller where to stop the NPC movement.
     */
    private final CitizensMoveController citizensMoveController;

    /**
     * Create a new NPCTeleportActionFactory.
     *
     * @param featureApi             the Feature API
     * @param citizensMoveController the move controller where to stop the NPC movement
     */
    public CitizensStopActionFactory(final FeatureApi featureApi, final CitizensMoveController citizensMoveController) {
        this.featureApi = featureApi;
        this.citizensMoveController = citizensMoveController;
    }

    @Override
    public PlayerlessAction parsePlayerless(final Instruction instruction) throws QuestException {
        return createCitizensStopAction(instruction);
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        return createCitizensStopAction(instruction);
    }

    private NullableActionAdapter createCitizensStopAction(final Instruction instruction) throws QuestException {
        final Argument<NpcID> npcId = instruction.parse(CitizensArgument.CITIZENS_ID).get();
        return new NullableActionAdapter(new CitizensStopAction(featureApi, npcId, citizensMoveController));
    }
}
