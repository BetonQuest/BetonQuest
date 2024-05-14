package org.betonquest.betonquest.compatibility.npcs.citizens;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.BQNPCAdapter;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.NPCSupplierStandard;
import org.betonquest.betonquest.exceptions.InstructionParseException;

import java.util.function.Supplier;

/**
 * Citizens supplier for the {@link BQNPCAdapter}.
 */
public interface CitizensNPCSupplier extends NPCSupplierStandard {
    @Override
    default Supplier<BQNPCAdapter> getSupplierByID(final String npcId) throws InstructionParseException {
        final int parsedId;
        try {
            parsedId = Integer.parseInt(npcId);
            if (parsedId < 0) {
                throw new InstructionParseException("The NPC ID '" + npcId + "' is not a positive or zero integer");
            }
        } catch (final NumberFormatException e) {
            throw new InstructionParseException("The NPC ID '" + npcId + "' is not a valid integer", e);
        }
        return () -> {
            final NPC npc = CitizensAPI.getNPCRegistry().getById(parsedId);
            return npc == null ? null : new CitizensBQAdapter(npc);
        };
    }
}
