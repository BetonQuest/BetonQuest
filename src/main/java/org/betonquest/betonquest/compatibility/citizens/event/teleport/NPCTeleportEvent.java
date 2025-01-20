package org.betonquest.betonquest.compatibility.citizens.event.teleport;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.SpawnReason;
import net.citizensnpcs.api.npc.NPC;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEvent;
import org.betonquest.betonquest.compatibility.citizens.CitizensIntegrator;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.Nullable;

/**
 * Stop the NPC when he is walking and teleport him to a given location.
 */
public class NPCTeleportEvent implements NullableEvent {
    /**
     * The location to teleport the NPC to.
     */
    private final VariableLocation location;

    /**
     * The NPC id.
     */
    private final int npcId;

    /**
     * Create a new NPCTeleportEvent.
     *
     * @param npcId    the id of the Citizens NPC, greater or equal to zero
     * @param location the location the NPC will be teleported to
     */
    public NPCTeleportEvent(final int npcId, final VariableLocation location) {
        this.npcId = npcId;
        this.location = location;
    }

    @Override
    public void execute(@Nullable final Profile profile) throws QuestException {
        final NPC npc = CitizensAPI.getNPCRegistry().getById(npcId);
        if (npc == null) {
            throw new QuestException("NPC with ID " + npcId + " does not exist");
        }
        CitizensIntegrator.getCitizensMoveInstance().stopNPCMoving(npc);
        npc.getNavigator().cancelNavigation();
        if (npc.isSpawned()) {
            npc.teleport(location.getValue(profile), PlayerTeleportEvent.TeleportCause.PLUGIN);
        } else {
            npc.spawn(location.getValue(profile), SpawnReason.PLUGIN);
        }
    }
}
