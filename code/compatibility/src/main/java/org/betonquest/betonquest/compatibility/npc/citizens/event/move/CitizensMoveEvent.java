package org.betonquest.betonquest.compatibility.npc.citizens.event.move;

import net.citizensnpcs.api.npc.NPC;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.feature.FeatureApi;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.npc.Npc;
import org.betonquest.betonquest.api.quest.npc.NpcID;

/**
 * Moves the NPC to a specified location, optionally firing doneEvents when it's done.
 */
public class CitizensMoveEvent implements PlayerEvent {

    /**
     * Feature API.
     */
    private final FeatureApi featureApi;

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
     * @param featureApi             the Feature API
     * @param npcId                  the ID of the NPC to move
     * @param citizensMoveController the move instance which handles the NPC movement
     * @param moveData               the parsed data for the NPC movement
     */
    public CitizensMoveEvent(final FeatureApi featureApi, final Variable<NpcID> npcId, final CitizensMoveController citizensMoveController,
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
            throw new QuestException("Can't use Citizens MoveEvent for non Citizens NPC");
        }
        if (profile.getOnlineProfile().isEmpty()) {
            citizensMoveController.stopNPCMoving(npc);
            return;
        }
        citizensMoveController.startNew(npc, profile, moveData);
    }
}
