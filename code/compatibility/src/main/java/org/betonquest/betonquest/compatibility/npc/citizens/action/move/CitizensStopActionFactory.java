package org.betonquest.betonquest.compatibility.npc.citizens.action.move;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.feature.FeatureApi;
import org.betonquest.betonquest.api.identifier.NpcIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.action.NullableActionAdapter;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.api.quest.action.PlayerlessAction;
import org.betonquest.betonquest.api.quest.action.PlayerlessActionFactory;
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
     * The Citizens argument parser.
     */
    private final CitizensArgument citizensArgument;

    /**
     * Create a new NPCTeleportActionFactory.
     *
     * @param featureApi             the Feature API
     * @param citizensArgument       the Citizens argument parser to use
     * @param citizensMoveController the move controller where to stop the NPC movement
     */
    public CitizensStopActionFactory(final FeatureApi featureApi, final CitizensArgument citizensArgument, final CitizensMoveController citizensMoveController) {
        this.featureApi = featureApi;
        this.citizensMoveController = citizensMoveController;
        this.citizensArgument = citizensArgument;
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
        final Argument<NpcIdentifier> npcId = instruction.parse(citizensArgument).get();
        return new NullableActionAdapter(new CitizensStopAction(featureApi, npcId, citizensMoveController));
    }
}
