package org.betonquest.betonquest.compatibility.npcs.abstractnpc.condition.region;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerlessConditionFactory;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.BQNPCAdapter;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.NPCAdapterSupplier;
import org.betonquest.betonquest.exceptions.InstructionParseException;

import java.util.function.Supplier;

/**
 * Factory to create {@link NPCRegionCondition}s from {@link Instruction}s.
 */
public class NPCRegionConditionFactory implements PlayerlessConditionFactory {
    /**
     * Providing a new NPC Adapter from an id.
     */
    private final NPCAdapterSupplier supplierStandard;

    /**
     * Create a new factory for NPC Region Conditions.
     *
     * @param supplierStandard the supplier providing the npc adapter
     */
    public NPCRegionConditionFactory(final NPCAdapterSupplier supplierStandard) {
        this.supplierStandard = supplierStandard;
    }

    @Override
    public PlayerlessCondition parsePlayerless(final Instruction instruction) throws InstructionParseException {
        final Supplier<BQNPCAdapter<?>> supplier = supplierStandard.getSupplierByID(instruction.next());
        final String region = instruction.next();
        return new NPCRegionCondition(supplier, region);
    }
}
