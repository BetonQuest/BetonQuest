package org.betonquest.betonquest.compatibility.npcs.abstractnpc;

import org.betonquest.betonquest.exceptions.InstructionParseException;

import java.util.function.Supplier;

public interface NPCSupplierStandard {
    /**
     * @param id of the npc
     * @return the supplier supplying the npc or null if none was found
     * @throws InstructionParseException if the id is invalid for the adapter
     */
    Supplier<BQNPCAdapter> getSupplierByID(String id) throws InstructionParseException;
}
