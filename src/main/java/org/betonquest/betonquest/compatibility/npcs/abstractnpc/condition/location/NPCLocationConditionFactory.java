package org.betonquest.betonquest.compatibility.npcs.abstractnpc.condition.location;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerlessConditionFactory;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableConditionAdapter;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.NPCAdapterSupplier;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.NPCAdapterSupplierSupplier;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;

/**
 * Factory to create {@link NPCLocationCondition}s from {@link Instruction}s.
 */
public class NPCLocationConditionFactory implements PlayerConditionFactory, PlayerlessConditionFactory {
    /**
     * Providing a new NPC Adapter from an id.
     */
    private final NPCAdapterSupplierSupplier supplierStandard;

    /**
     * Create a new factory for NPC Location Conditions.
     *
     * @param supplierStandard the supplier providing the npc adapter
     */
    public NPCLocationConditionFactory(final NPCAdapterSupplierSupplier supplierStandard) {
        this.supplierStandard = supplierStandard;
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
        final NPCAdapterSupplier supplier = supplierStandard.getSupplierByID(npcId);
        final VariableLocation location = instruction.getLocation();
        final VariableNumber radius = instruction.getVarNum();
        return new NullableConditionAdapter(new NPCLocationCondition(supplier, location, radius));
    }
}
