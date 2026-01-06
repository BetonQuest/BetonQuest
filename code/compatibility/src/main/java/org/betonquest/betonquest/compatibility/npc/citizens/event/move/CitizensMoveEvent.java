package org.betonquest.betonquest.compatibility.npc.citizens.event.move;

import net.citizensnpcs.api.npc.NPC;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.feature.FeatureApi;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.npc.Npc;
import org.betonquest.betonquest.api.quest.npc.NpcID;

/**
 * Moves the NPC to a specified location, optionally firing doneActions when it's done.
 */
public class CitizensMoveEvent implements PlayerAction {

    /**
     * Feature API.
     */
    private final FeatureApi featureApi;

    /**
     * ID of the NPC to move.
     */
    private final Argument<NpcID> npcId;

    /**
     * Move Instance which handles the NPC movement.
     */
    private final CitizensMoveController citizensMoveController;

    /**
     * Parsed data for the NPC movement.
     */
    private final CitizensMoveController.MoveData moveData;

    /**
     * Create a new CitizensMoveAction.
     *
     * @param featureApi             the Feature API
     * @param npcId                  the ID of the NPC to move
     * @param citizensMoveController the move instance which handles the NPC movement
     * @param moveData               the parsed data for the NPC movement
     */
    public CitizensMoveEvent(final FeatureApi featureApi, final Argument<NpcID> npcId, final CitizensMoveController citizensMoveController,
                             final CitizensMoveController.MoveData moveData) {
        this.featureApi = featureApi;
        this.npcId = npcId;
        this.citizensMoveController = citizensMoveController;
        this.moveData = moveData;
    }

    @Override
    public void execute(final Profile profile) throws QuestException {
        final Npc<?> bqNpc = featureApi.getNpc(npcId.getValue(profile), profile);
        if (!(bqNpc.getOriginal() instanceof final NPC npc)) {
            throw new QuestException("Can't use Citizens MoveAction for non Citizens NPC");
        }
        if (profile.getOnlineProfile().isEmpty()) {
            citizensMoveController.stopNPCMoving(npc);
            return;
        }
        citizensMoveController.startNew(npc, profile, moveData);
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
