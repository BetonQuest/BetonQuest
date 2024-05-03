package org.betonquest.betonquest.compatibility.npcs.citizens;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.BQNPCAdapter;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.NPCSupplierStandard;
import org.betonquest.betonquest.exceptions.InstructionParseException;

import java.util.function.Supplier;

public interface CitizensNPCSupplier extends NPCSupplierStandard {
    @Override
    default Supplier<BQNPCAdapter> getSupplierByID(final String id) throws InstructionParseException {
        final int npcId;
        try {
            npcId = Integer.parseInt(id);
            if (npcId < 0) {
                throw new InstructionParseException("The specified NPC ID was not a positive or zero integer");
            }
        } catch (final NumberFormatException e) {
            throw new InstructionParseException("The specified NPC ID was not a valid integer", e);
        }
        return () -> {
            final NPC npc = CitizensAPI.getNPCRegistry().getById(npcId);
            return npc == null ? null : new CitizensBQAdapter(npc);
        };
    }
}
