package org.betonquest.betonquest.compatibility.npc.citizens.event.move;

import net.citizensnpcs.api.npc.NPC;
import org.betonquest.betonquest.api.feature.FeatureAPI;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.npc.Npc;
import org.betonquest.betonquest.id.NpcID;
import org.betonquest.betonquest.instruction.variable.Variable;

/**
 * Moves the NPC to a specified location, optionally firing doneEvents when it's done.
 */
public class CitizensMoveEvent implements PlayerEvent {

    /**
     * Feature API.
     */
    private final FeatureAPI featureAPI;

    /**
     * ID of the NPC to move.
     */
    private final Variable<NpcID> npcId;

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
     * @param featureAPI             the Feature API
     * @param npcId                  the ID of the NPC to move
     * @param citizensMoveController the move instance which handles the NPC movement
     * @param moveData               the parsed data for the NPC movement
     */
    public CitizensMoveEvent(final FeatureAPI featureAPI, final Variable<NpcID> npcId, final CitizensMoveController citizensMoveController,
                             final CitizensMoveController.MoveData moveData) {
        this.featureAPI = featureAPI;
        this.npcId = npcId;
        this.citizensMoveController = citizensMoveController;
        this.moveData = moveData;
    }

    @Override
    public void execute(final Profile profile) throws QuestException {
        final Npc<?> bqNpc = featureAPI.getNpc(npcId.getValue(profile), profile);
        if (!(bqNpc.getOriginal() instanceof final NPC npc)) {
            throw new QuestException("Can't use Citizens MoveEvent for non Citizens NPC");
        }
        if (profile.getOnlineProfile().isEmpty()) {
            citizensMoveController.stopNPCMoving(npc);
            return;
        }
        citizensMoveController.startNew(npc, profile, moveData);
    }
}
