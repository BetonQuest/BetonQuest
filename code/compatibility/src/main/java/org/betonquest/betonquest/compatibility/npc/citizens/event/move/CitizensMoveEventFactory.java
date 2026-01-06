package org.betonquest.betonquest.compatibility.npc.citizens.event.move;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.feature.FeatureApi;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.action.ActionID;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.api.quest.npc.NpcID;
import org.betonquest.betonquest.compatibility.npc.citizens.CitizensArgument;
import org.bukkit.Location;

import java.util.Collections;
import java.util.List;

/**
 * Factory for {@link CitizensMoveEvent} from the {@link Instruction}.
 */
public class CitizensMoveEventFactory implements PlayerActionFactory {

    /**
     * Feature API.
     */
    private final FeatureApi featureApi;

    /**
     * Move instance to handle movement of Citizens NPCs.
     */
    private final CitizensMoveController citizensMoveController;

    /**
     * Create a new NPCTeleportEventFactory.
     *
     * @param featureApi             the Feature API
     * @param citizensMoveController the move instance to handle movement of Citizens NPCs
     */
    public CitizensMoveEventFactory(final FeatureApi featureApi, final CitizensMoveController citizensMoveController) {
        this.featureApi = featureApi;
        this.citizensMoveController = citizensMoveController;
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<NpcID> npcId = instruction.parse(CitizensArgument.CITIZENS_ID).get();
        final Argument<List<Location>> locations = instruction.location().list().invalidate(List::isEmpty).get();
        final Argument<Number> waitTicks = instruction.number().get("wait", 0);
        final Argument<List<ActionID>> doneEvents = instruction.parse(ActionID::new).list().get("done", Collections.emptyList());
        final Argument<List<ActionID>> failEvents = instruction.parse(ActionID::new).list().get("fail", Collections.emptyList());
        final FlagArgument<Boolean> blockConversations = instruction.bool().getFlag("block", true);
        final CitizensMoveController.MoveData moveAction = new CitizensMoveController.MoveData(locations, waitTicks,
                doneEvents, failEvents, blockConversations);
        return new CitizensMoveEvent(featureApi, npcId, citizensMoveController, moveAction);
    }
}
