package org.betonquest.betonquest.compatibility.citizens.event.move;

import org.betonquest.betonquest.api.feature.FeatureAPI;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.id.NpcID;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadEvent;

import java.util.List;

/**
 * Factory for {@link CitizensMoveEvent} from the {@link Instruction}.
 */
public class CitizensMoveEventFactory implements PlayerEventFactory {

    /**
     * Feature API.
     */
    private final FeatureAPI featureAPI;

    /**
     * Data to use for syncing to the primary server thread.
     */
    private final PrimaryServerThreadData data;

    /**
     * Move instance to handle movement of Citizens NPCs.
     */
    private final CitizensMoveController citizensMoveController;

    /**
     * Create a new NPCTeleportEventFactory.
     *
     * @param featureAPI             the Feature API
     * @param data                   the data to use for syncing to the primary server thread
     * @param citizensMoveController the move instance to handle movement of Citizens NPCs
     */
    public CitizensMoveEventFactory(final FeatureAPI featureAPI, final PrimaryServerThreadData data, final CitizensMoveController citizensMoveController) {
        this.featureAPI = featureAPI;
        this.data = data;
        this.citizensMoveController = citizensMoveController;
    }

    @Override
    @SuppressWarnings("PMD.PrematureDeclaration")
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        final NpcID npcId = instruction.getID(NpcID::new);
        final Instruction npcInstruction = npcId.getInstruction();
        if (!"citizens".equals(npcInstruction.getPart(0))) {
            throw new QuestException("Cannot use non-Citizens NPC ID!");
        }
        final List<VariableLocation> locations = instruction.getList(VariableLocation::new);
        if (locations.isEmpty()) {
            throw new QuestException("Not enough arguments");
        }
        final int waitTicks = instruction.getInt(instruction.getOptional("wait"), 0);
        final List<EventID> doneEvents = instruction.getIDList(instruction.getOptional("done"), EventID::new);
        final List<EventID> failEvents = instruction.getIDList(instruction.getOptional("fail"), EventID::new);
        final boolean blockConversations = instruction.hasArgument("block");
        final CitizensMoveController.MoveData moveAction = new CitizensMoveController.MoveData(locations, waitTicks,
                doneEvents, failEvents, blockConversations, instruction.getPackage());
        return new PrimaryServerThreadEvent(new CitizensMoveEvent(featureAPI, npcId, citizensMoveController, moveAction), data);
    }
}
