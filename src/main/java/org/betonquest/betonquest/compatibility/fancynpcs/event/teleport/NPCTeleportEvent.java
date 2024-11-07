package org.betonquest.betonquest.compatibility.fancynpcs.event.teleport;

import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import de.oliver.fancynpcs.api.Npc;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEvent;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
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
    private final String npcId;

    /**
     * Create a new NPCTeleportEvent.
     *
     * @param npcId    the id of the FancyNpcs NPC, greater or equal to zero
     * @param location the location the NPC will be teleported to
     */
    public NPCTeleportEvent(final String npcId, final VariableLocation location) {
        this.npcId = npcId;
        this.location = location;
    }

    @Override
    public void execute(@Nullable final Profile profile) throws QuestRuntimeException {
        final Npc npc = FancyNpcsPlugin.get().getNpcManager().getNpcById(npcId);

        if (npc == null) {
            throw new QuestRuntimeException("NPC with ID " + npcId + " does not exist");
        }

        npc.getData().setLocation(location.getValue(profile));
        npc.updateForAll();
    }
}
