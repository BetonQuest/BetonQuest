package org.betonquest.betonquest.compatibility.citizens.event.move;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadEvent;

import java.util.List;

/**
 * Factory for {@link CitizensMoveEvent} from the {@link Instruction}.
 */
public class CitizensMoveEventFactory implements EventFactory {
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
     * @param data                   the data to use for syncing to the primary server thread
     * @param citizensMoveController the move instance to handle movement of Citizens NPCs
     */
    public CitizensMoveEventFactory(final PrimaryServerThreadData data, final CitizensMoveController citizensMoveController) {
        this.data = data;
        this.citizensMoveController = citizensMoveController;
    }

    @Override
    @SuppressWarnings("PMD.PrematureDeclaration")
    public Event parseEvent(final Instruction instruction) throws QuestException {
        final int npcId = instruction.getInt();
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
        return new PrimaryServerThreadEvent(new CitizensMoveEvent(npcId, citizensMoveController, moveAction), data);
    }
}
