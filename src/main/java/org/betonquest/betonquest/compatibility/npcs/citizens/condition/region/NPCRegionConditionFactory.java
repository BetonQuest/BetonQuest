package org.betonquest.betonquest.compatibility.npcs.citizens.condition.region;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerlessConditionFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.condition.PrimaryServerThreadPlayerlessCondition;

/**
 * Factory to create {@link NPCRegionCondition}s from {@link Instruction}s.
 */
public class NPCRegionConditionFactory implements PlayerlessConditionFactory {
    /**
     * Data used for primary server thread access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create a new factory for NPC Region Conditions.
     *
     * @param data the data for primary server thread access
     */
    public NPCRegionConditionFactory(final PrimaryServerThreadData data) {
        this.data = data;
    }

    @Override
    public PlayerlessCondition parsePlayerless(final Instruction instruction) throws InstructionParseException {
        final int npcId = instruction.getInt();
        if (npcId < 0) {
            throw new InstructionParseException("NPC ID cannot be less than 0");
        }
        final String region = instruction.next();
        return new PrimaryServerThreadPlayerlessCondition(new NPCRegionCondition(npcId, region), data);
    }
}
