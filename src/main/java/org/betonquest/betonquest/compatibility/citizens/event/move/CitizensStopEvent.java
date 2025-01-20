package org.betonquest.betonquest.compatibility.citizens.event.move;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.StaticEvent;

/**
 * Stop the NPC when he is walking.
 */
public class CitizensStopEvent implements StaticEvent {
    /**
     * ID of the NPC to stop.
     */
    private final int npcId;

    /**
     * Move Controller where to stop NPC movement.
     */
    private final CitizensMoveController citizensMoveController;

    /**
     * Create a new CitizensStopEvent.
     *
     * @param npcId                  the id of the NPC to stop
     * @param citizensMoveController the move controller where to stop NPC movement
     */
    public CitizensStopEvent(final int npcId, final CitizensMoveController citizensMoveController) {
        this.npcId = npcId;
        this.citizensMoveController = citizensMoveController;
    }

    @Override
    public void execute() throws QuestException {
        final NPC npc = CitizensAPI.getNPCRegistry().getById(npcId);
        if (npc == null) {
            throw new QuestException("NPC with ID " + npcId + " does not exist");
        }
        citizensMoveController.stopNPCMoving(npc);
        npc.getNavigator().cancelNavigation();
    }
}
