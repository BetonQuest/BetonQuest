package org.betonquest.betonquest.compatibility.npcs.citizens.condition;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.NPCAdapterSupplierSupplier;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.condition.location.NPCLocationCondition;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.condition.location.NPCLocationConditionFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.condition.PrimaryServerThreadPlayerCondition;
import org.betonquest.betonquest.quest.condition.PrimaryServerThreadPlayerlessCondition;

/**
 * Citizens implementation of {@link NPCLocationCondition}.
 */
public class CitizensLocationConditionFactory extends NPCLocationConditionFactory {
    /**
     * Data used for primary server thread access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create a new factory for Citizens NPC location conditions.
     *
     * @param supplierStandard the supplier providing the npc adapter
     * @param data             the data used for primary server thread access
     */
    public CitizensLocationConditionFactory(final NPCAdapterSupplierSupplier supplierStandard, final PrimaryServerThreadData data) {
        super(supplierStandard);
        this.data = data;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws InstructionParseException {
        return new PrimaryServerThreadPlayerCondition(super.parsePlayer(instruction), data);
    }

    @Override
    public PlayerlessCondition parsePlayerless(final Instruction instruction) throws InstructionParseException {
        return new PrimaryServerThreadPlayerlessCondition(super.parsePlayerless(instruction), data);
    }
}
