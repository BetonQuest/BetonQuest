package org.betonquest.betonquest.compatibility.npcs.abstractnpc.condition.location;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerlessConditionFactory;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableConditionAdapter;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.BQNPCAdapter;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.NPCFactory;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.NPCSupplierStandard;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;

import java.util.function.Supplier;

/**
 * Factory to create {@link NPCLocationCondition}s from {@link Instruction}s.
 */
public class NPCLocationConditionFactory extends NPCFactory implements PlayerConditionFactory, PlayerlessConditionFactory {
    /**
     * Create a new factory for NPC Location Conditions.
     *
     * @param supplierSupplier the supplier providing the npc adapter supplier
     */
    public NPCLocationConditionFactory(final Supplier<NPCSupplierStandard> supplierSupplier) {
        super(supplierSupplier);
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws InstructionParseException {
        return parseNpcLocationCondition(instruction);
    }

    @Override
    public PlayerlessCondition parsePlayerless(final Instruction instruction) throws InstructionParseException {
        return parseNpcLocationCondition(instruction);
    }

    private NullableConditionAdapter parseNpcLocationCondition(final Instruction instruction) throws InstructionParseException {
        final String npcId = instruction.next();
        final Supplier<BQNPCAdapter<?>> supplier = getSupplierByID(npcId);
        final VariableLocation location = instruction.getLocation();
        final VariableNumber radius = instruction.getVarNum();
        return new NullableConditionAdapter(new NPCLocationCondition(npcId, supplier, location, radius));
    }
}
