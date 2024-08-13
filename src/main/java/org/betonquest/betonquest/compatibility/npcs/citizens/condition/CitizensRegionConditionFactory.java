package org.betonquest.betonquest.compatibility.npcs.citizens.condition;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.NPCAdapterSupplier;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.condition.region.NPCRegionCondition;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.condition.region.NPCRegionConditionFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.condition.PrimaryServerThreadPlayerlessCondition;

/**
 * Citizens implementation of {@link NPCRegionCondition}.
 */
public class CitizensRegionConditionFactory extends NPCRegionConditionFactory {
    /**
     * Data used for primary server thread access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create a new factory for Citizens NPC region conditions.
     *
     * @param supplierStandard the supplier providing the npc adapter supplier
     * @param data             the data used for primary server thread access
     */
    public CitizensRegionConditionFactory(final NPCAdapterSupplier supplierStandard, final PrimaryServerThreadData data) {
        super(supplierStandard);
        this.data = data;
    }

    @Override
    public PlayerlessCondition parsePlayerless(final Instruction instruction) throws InstructionParseException {
        return new PrimaryServerThreadPlayerlessCondition(super.parsePlayerless(instruction), data);
    }
}