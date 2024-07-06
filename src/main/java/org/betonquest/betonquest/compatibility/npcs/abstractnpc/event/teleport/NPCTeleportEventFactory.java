package org.betonquest.betonquest.compatibility.npcs.abstractnpc.event.teleport;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.api.quest.event.StaticEvent;
import org.betonquest.betonquest.api.quest.event.StaticEventFactory;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEventAdapter;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.BQNPCAdapter;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.NPCFactory;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.NPCSupplierStandard;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;

import java.util.function.Supplier;

/**
 * Factory for {@link NPCTeleportEvent} from the {@link Instruction}.
 */
public abstract class NPCTeleportEventFactory extends NPCFactory implements EventFactory, StaticEventFactory, NPCSupplierStandard {
    /**
     * Create a new factory for NPC Teleport Events.
     *
     * @param supplierStandard the supplier providing the npc adapter supplier
     */
    public NPCTeleportEventFactory(final NPCSupplierStandard supplierStandard) {
        super(supplierStandard);
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws InstructionParseException {
        return createNpcTeleportEvent(instruction);
    }

    @Override
    public StaticEvent parseStaticEvent(final Instruction instruction) throws InstructionParseException {
        return createNpcTeleportEvent(instruction);
    }

    private NullableEventAdapter createNpcTeleportEvent(final Instruction instruction) throws InstructionParseException {
        final String npcId = instruction.next();
        final Supplier<BQNPCAdapter<?>> npcSupplier = getSupplierByID(npcId);
        final VariableLocation location = instruction.getLocation();
        return new NullableEventAdapter(new NPCTeleportEvent(npcId, npcSupplier, location));
    }
}
