package org.betonquest.betonquest.compatibility.citizens.event.move;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.exceptions.QuestException;

/**
 * Moves the NPC to a specified location, optionally firing doneEvents when it's done.
 */
public class CitizensMoveEvent implements Event {
    /**
     * ID of the NPC to move.
     */
    private final int npcId;

    /**
     * Move Instance which handles the NPC movement.
     */
    private final CitizensMoveController citizensMoveController;

    /**
     * Parsed data for the NPC movement.
     */
    private final CitizensMoveController.MoveData moveData;

    /**
     * Create a new CitizensMoveEvent.
     *
     * @param npcId                  the ID of the NPC to move
     * @param citizensMoveController the move instance which handles the NPC movement
     * @param moveData               the parsed data for the NPC movement
     */
    public CitizensMoveEvent(final int npcId, final CitizensMoveController citizensMoveController, final CitizensMoveController.MoveData moveData) {
        this.npcId = npcId;
        this.citizensMoveController = citizensMoveController;
        this.moveData = moveData;
    }

    @Override
    public void execute(final Profile profile) throws QuestException {
        // this event should not run if the player is offline
        final NPC npc = CitizensAPI.getNPCRegistry().getById(npcId);
        if (npc == null) {
            throw new QuestException("NPC with ID " + npcId + " does not exist");
        }
        if (profile.getOnlineProfile().isEmpty()) {
            citizensMoveController.stopNPCMoving(npc);
            return;
        }
        citizensMoveController.startNew(npc, profile, moveData);
    }
}
