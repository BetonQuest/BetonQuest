package org.betonquest.betonquest.compatibility.npcs.abstractnpc.event.teleport;

import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEvent;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.BQNPCAdapter;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.NPCUtil;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * Teleport NPC to a given location.
 */
public class NPCTeleportEvent implements NullableEvent {
    /**
     * NPC ID used in getNPC error message.
     */
    protected final String npcId;

    /**
     * Supplying the npc instance.
     */
    protected final Supplier<BQNPCAdapter<?>> npcSupplier;

    /**
     * The location to teleport the NPC to.
     */
    protected final VariableLocation location;

    /**
     * Create a new NPCTeleportEvent.
     *
     * @param npcId       the identifier shown when the supplier did not return a NPC
     * @param npcSupplier the supplier of the NPC
     * @param location    the location the NPC will be teleported to
     */
    public NPCTeleportEvent(final String npcId, final Supplier<BQNPCAdapter<?>> npcSupplier, final VariableLocation location) {
        this.npcId = npcId;
        this.npcSupplier = npcSupplier;
        this.location = location;
    }

    @Override
    public void execute(@Nullable final Profile profile) throws QuestRuntimeException {
        NPCUtil.getNPC(npcSupplier, npcId).teleport(location.getValue(profile));
    }
}
