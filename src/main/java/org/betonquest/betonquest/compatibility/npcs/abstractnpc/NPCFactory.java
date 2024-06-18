package org.betonquest.betonquest.compatibility.npcs.abstractnpc;

import org.betonquest.betonquest.exceptions.InstructionParseException;

import java.util.function.Supplier;

/**
 * Lazy storage of the Supplier which providing the method to get the actual BQNPCAdapter.
 */
public class NPCFactory implements NPCSupplierStandard {
    /**
     * Supplier providing the method to actually get a new NPC Adapter.
     */
    private final Supplier<NPCSupplierStandard> supplierSupplier;

    /**
     * Lazy storage for the supplier.
     *
     * @param supplierSupplier the supplier providing the npc adapter supplier
     */
    protected NPCFactory(final Supplier<NPCSupplierStandard> supplierSupplier) {
        this.supplierSupplier = supplierSupplier;
    }

    @Override
    public Supplier<BQNPCAdapter> getSupplierByID(final String npcId) throws InstructionParseException {
        return supplierSupplier.get().getSupplierByID(npcId);
    }
}
