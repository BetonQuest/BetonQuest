package org.betonquest.betonquest.compatibility.npc.citizens.event.move;

import org.betonquest.betonquest.api.feature.FeatureApi;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.instruction.variable.VariableList;
import org.betonquest.betonquest.api.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.EventID;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.thread.PrimaryServerThreadEvent;
import org.betonquest.betonquest.api.quest.npc.NpcID;
import org.betonquest.betonquest.compatibility.npc.citizens.CitizensArgument;
import org.bukkit.Location;

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
     * @param featureApi             the Feature API
     * @param data                   the data to use for syncing to the primary server thread
     * @param citizensMoveController the move instance to handle movement of Citizens NPCs
     */
    public CitizensMoveEventFactory(final FeatureApi featureApi, final PrimaryServerThreadData data, final CitizensMoveController citizensMoveController) {
        this.featureApi = featureApi;
        this.data = data;
        this.citizensMoveController = citizensMoveController;
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        final Variable<NpcID> npcId = instruction.get(CitizensArgument.CITIZENS_ID);
        final Variable<List<Location>> locations = instruction.getList(Argument.LOCATION, VariableList.notEmptyChecker());
        final Variable<Number> waitTicks = instruction.getValue("wait", Argument.NUMBER, 0);
        final Variable<List<EventID>> doneEvents = instruction.getValueList("done", EventID::new);
        final Variable<List<EventID>> failEvents = instruction.getValueList("fail", EventID::new);
        final boolean blockConversations = instruction.hasArgument("block");
        final CitizensMoveController.MoveData moveAction = new CitizensMoveController.MoveData(locations, waitTicks,
                doneEvents, failEvents, blockConversations);
        return new PrimaryServerThreadEvent(new CitizensMoveEvent(featureApi, npcId, citizensMoveController, moveAction), data);
    }
}
