package org.betonquest.betonquest.compatibility.npc.citizens.event.move;

import net.citizensnpcs.api.npc.NPC;
import org.betonquest.betonquest.api.feature.FeatureAPI;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEvent;
import org.betonquest.betonquest.api.quest.npc.Npc;
import org.betonquest.betonquest.id.NpcID;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.jetbrains.annotations.Nullable;

/**
 * Stop the NPC when he is walking.
 */
public class CitizensStopEvent implements NullableEvent {

    /**
     * Feature API.
     */
    private final FeatureAPI featureAPI;

    /**
     * ID of the NPC to stop.
     */
    private final Variable<NpcID> npcId;

    /**
     * Move Controller where to stop NPC movement.
     */
    private final CitizensMoveController citizensMoveController;

    /**
     * Create a new CitizensStopEvent.
     *
     * @param featureAPI             the Feature API
     * @param npcId                  the id of the NPC to stop
     * @param citizensMoveController the move controller where to stop NPC movement
     */
    public CitizensStopEvent(final FeatureAPI featureAPI, final Variable<NpcID> npcId, final CitizensMoveController citizensMoveController) {
        this.featureAPI = featureAPI;
        this.npcId = npcId;
        this.citizensMoveController = citizensMoveController;
    }

    @Override
    public void execute(@Nullable final Profile profile) throws QuestException {
        final Npc<?> bqNpc = featureAPI.getNpc(npcId.getValue(profile), profile);
        if (!(bqNpc.getOriginal() instanceof final NPC npc)) {
            throw new QuestException("Can't use non Citizens NPC!");
        }
        citizensMoveController.stopNPCMoving(npc);
        npc.getNavigator().cancelNavigation();
    }
}
