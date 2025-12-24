package org.betonquest.betonquest.compatibility.npc.citizens.event.move;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.feature.FeatureApi;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.event.EventID;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.npc.NpcID;
import org.betonquest.betonquest.compatibility.npc.citizens.CitizensArgument;
import org.bukkit.Location;

import java.util.Collections;
import java.util.List;

/**
 * Factory for {@link CitizensMoveEvent} from the {@link Instruction}.
 */
public class CitizensMoveEventFactory implements PlayerEventFactory {

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
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        final Variable<NpcID> npcId = instruction.parse(CitizensArgument.CITIZENS_ID).get();
        final Variable<List<Location>> locations = instruction.location().getList();
        final Variable<Number> waitTicks = instruction.number().get("wait", 0);
        final Variable<List<EventID>> doneEvents = instruction.parse(EventID::new).getList("done", Collections.emptyList());
        final Variable<List<EventID>> failEvents = instruction.parse(EventID::new).getList("fail", Collections.emptyList());
        final boolean blockConversations = instruction.hasArgument("block");
        final CitizensMoveController.MoveData moveAction = new CitizensMoveController.MoveData(locations, waitTicks,
                doneEvents, failEvents, blockConversations);
        return new CitizensMoveEvent(featureApi, npcId, citizensMoveController, moveAction);
    }
}
