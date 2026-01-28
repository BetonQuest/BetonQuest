package org.betonquest.betonquest.compatibility.npc.citizens.action.move;

import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.NpcIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.api.quest.action.PlayerlessAction;
import org.betonquest.betonquest.api.quest.action.PlayerlessActionFactory;
import org.betonquest.betonquest.api.quest.action.nullable.NullableActionAdapter;
import org.betonquest.betonquest.compatibility.npc.citizens.CitizensArgument;

/**
 * Factory for {@link CitizensStopAction} from the {@link Instruction}.
 */
public class CitizensStopActionFactory implements PlayerlessActionFactory, PlayerActionFactory {

    /**
     * Feature API.
     */
    private final BetonQuestApi betonQuestApi;

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
     * @param betonQuestApi          the Feature API
     * @param citizensMoveController the move controller where to stop the NPC movement
     * @throws QuestException an exception if the identifier factory cannot be retrieved
     */
    public CitizensStopActionFactory(final BetonQuestApi betonQuestApi, final CitizensMoveController citizensMoveController) throws QuestException {
        this.betonQuestApi = betonQuestApi;
        this.citizensMoveController = citizensMoveController;
        this.citizensArgument = new CitizensArgument(betonQuestApi.getInstructionApi(),
                betonQuestApi.getQuestRegistries().identifiers().getFactory(NpcIdentifier.class));
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
        return new NullableActionAdapter(new CitizensStopAction(betonQuestApi.getFeatureApi(), npcId, citizensMoveController));
    }
}
