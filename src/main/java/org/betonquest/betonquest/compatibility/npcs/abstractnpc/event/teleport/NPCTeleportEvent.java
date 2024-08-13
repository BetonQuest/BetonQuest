package org.betonquest.betonquest.compatibility.npcs.abstractnpc.event.teleport;

import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEvent;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.NPCAdapterSupplier;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.jetbrains.annotations.Nullable;

/**
 * Teleport NPC to a given location.
 */
public class NPCTeleportEvent implements NullableEvent {
    /**
     * Supplying the npc instance.
     */
    protected final NPCAdapterSupplier npcSupplier;

    /**
     * The location to teleport the NPC to.
     */
    protected final VariableLocation location;

    /**
     * Create a new NPCTeleportEvent.
     *
     * @param npcSupplier the supplier of the NPC
     * @param location    the location the NPC will be teleported to
     */
    public NPCTeleportEvent(final NPCAdapterSupplier npcSupplier, final VariableLocation location) {
        this.npcSupplier = npcSupplier;
        this.location = location;
    }

    @Override
    public void execute(@Nullable final Profile profile) throws QuestRuntimeException {
        npcSupplier.get().teleport(location.getValue(profile));
    }
}
