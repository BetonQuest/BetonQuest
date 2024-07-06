package org.betonquest.betonquest.compatibility.npcs.abstractnpc.condition.region;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerlessConditionFactory;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.BQNPCAdapter;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.NPCFactory;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.NPCSupplierStandard;
import org.betonquest.betonquest.exceptions.InstructionParseException;

import java.util.function.Supplier;

/**
 * Factory to create {@link NPCRegionCondition}s from {@link Instruction}s.
 */
public class NPCRegionConditionFactory extends NPCFactory implements PlayerlessConditionFactory {
    /**
     * Create a new factory for NPC Region Conditions.
     *
     * @param supplierStandard the supplier providing the npc adapter supplier
     */
    public NPCRegionConditionFactory(final NPCSupplierStandard supplierStandard) {
        super(supplierStandard);
    }

    @Override
    public PlayerlessCondition parsePlayerless(final Instruction instruction) throws InstructionParseException {
        final Supplier<BQNPCAdapter<?>> supplier = getSupplierByID(instruction.next());
        final String region = instruction.next();
        return new NPCRegionCondition(supplier, region);
    }
}
