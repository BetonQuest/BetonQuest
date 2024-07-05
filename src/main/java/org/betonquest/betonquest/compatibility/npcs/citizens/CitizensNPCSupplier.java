package org.betonquest.betonquest.compatibility.npcs.citizens;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.BQNPCAdapter;
import org.betonquest.betonquest.exceptions.InstructionParseException;

import java.util.function.Supplier;

/**
 * Citizens supplier for the {@link BQNPCAdapter}.
 */
public interface CitizensNPCSupplier {
    /**
     * Gets a supplier which will return a new Citizens {@link BQNPCAdapter}
     * if the {@code npcID} has a valid npc or null.
     *
     * @param npcId the id of the Citizens npc
     * @return the supplier which will return the npc or null if none was found by the npcId
     * @throws InstructionParseException when the id cannot be parsed as positive or zero integer
     */
    static Supplier<BQNPCAdapter<?>> getSupplierByIDStatic(final String npcId) throws InstructionParseException {
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
