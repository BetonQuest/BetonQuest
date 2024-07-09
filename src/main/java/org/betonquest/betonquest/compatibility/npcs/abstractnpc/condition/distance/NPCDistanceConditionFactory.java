package org.betonquest.betonquest.compatibility.npcs.abstractnpc.condition.distance;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.BQNPCAdapter;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.NPCSupplierStandard;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;

import java.util.function.Supplier;

/**
 * Factory to create {@link NPCDistanceCondition}s from {@link Instruction}s.
 */
public class NPCDistanceConditionFactory implements PlayerConditionFactory {
    /**
     * Providing a new NPC Adapter from an id.
     */
    private final NPCSupplierStandard supplierStandard;

    /**
     * Create a new factory for NPC Distance Conditions.
     *
     * @param supplierStandard the supplier providing the npc adapter
     */
    public NPCDistanceConditionFactory(final NPCSupplierStandard supplierStandard) {
        this.supplierStandard = supplierStandard;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws InstructionParseException {
        final String npcId = instruction.next();
        final Supplier<BQNPCAdapter<?>> supplier = supplierStandard.getSupplierByID(npcId);
        final VariableNumber distance = instruction.getVarNum();
        return new NPCDistanceCondition(npcId, supplier, distance);
    }
}
