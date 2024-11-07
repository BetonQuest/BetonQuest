package org.betonquest.betonquest.compatibility.fancynpcs.condition.region;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerlessConditionFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.condition.PrimaryServerThreadPlayerlessCondition;
import org.betonquest.betonquest.utils.Utils;

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
        final String npcId = instruction.next();
        if (!Utils.isUUID(npcId)) {
            throw new InstructionParseException("NPC ID isn't a valid UUID");
        }
        final String region = instruction.next();
        return new PrimaryServerThreadPlayerlessCondition(new NPCRegionCondition(npcId, region), data);
    }
}
