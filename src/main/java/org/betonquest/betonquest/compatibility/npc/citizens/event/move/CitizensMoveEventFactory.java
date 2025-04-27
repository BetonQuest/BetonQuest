package org.betonquest.betonquest.compatibility.npc.citizens.event.move;

import org.betonquest.betonquest.api.feature.FeatureAPI;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.compatibility.npc.citizens.CitizensArgument;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.id.NpcID;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.argument.IDArgument;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.betonquest.betonquest.instruction.variable.VariableList;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadEvent;
import org.bukkit.Location;

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
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        final Variable<NpcID> npcId = instruction.get(CitizensArgument.CITIZENS_ID);
        final VariableList<Location> locations = instruction.get(Argument.ofList(Argument.LOCATION, VariableList.notEmptyChecker()));
        final Variable<Number> waitTicks = instruction.getVariable(instruction.getOptional("wait"), Argument.NUMBER, 0);
        final VariableList<EventID> doneEvents = instruction.get(instruction.getOptional("done", ""), IDArgument.ofList(EventID::new));
        final VariableList<EventID> failEvents = instruction.get(instruction.getOptional("fail", ""), IDArgument.ofList(EventID::new));
        final boolean blockConversations = instruction.hasArgument("block");
        final CitizensMoveController.MoveData moveAction = new CitizensMoveController.MoveData(locations, waitTicks,
                doneEvents, failEvents, blockConversations);
        return new PrimaryServerThreadEvent(new CitizensMoveEvent(featureAPI, npcId, citizensMoveController, moveAction), data);
    }
}
