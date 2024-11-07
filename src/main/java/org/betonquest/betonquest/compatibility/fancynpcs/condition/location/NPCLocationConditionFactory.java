package org.betonquest.betonquest.compatibility.fancynpcs.condition.location;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerlessConditionFactory;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableConditionAdapter;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.condition.PrimaryServerThreadPlayerCondition;
import org.betonquest.betonquest.quest.condition.PrimaryServerThreadPlayerlessCondition;
import org.betonquest.betonquest.utils.Utils;

/**
 * Factory to create {@link NPCLocationCondition}s from {@link Instruction}s.
 */
public class NPCLocationConditionFactory implements PlayerConditionFactory, PlayerlessConditionFactory {
    /**
     * Data used for primary server thread access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create a new factory for NPC Location Conditions.
     *
     * @param data the data for primary server thread access
     */
    public NPCLocationConditionFactory(final PrimaryServerThreadData data) {
        this.data = data;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws InstructionParseException {
        return new PrimaryServerThreadPlayerCondition(parseNpcLocationCondition(instruction), data);
    }

    @Override
    public PlayerlessCondition parsePlayerless(final Instruction instruction) throws InstructionParseException {
        return new PrimaryServerThreadPlayerlessCondition(parseNpcLocationCondition(instruction), data);
    }

    private NullableConditionAdapter parseNpcLocationCondition(final Instruction instruction) throws InstructionParseException {
        final String npcId = instruction.next();
        if (!Utils.isUUID(npcId)) {
            throw new InstructionParseException("NPC ID isn't a valid UUID");
        }
        final VariableLocation location = instruction.getLocation();
        final VariableNumber radius = instruction.getVarNum();
        return new NullableConditionAdapter(new NPCLocationCondition(npcId, location, radius));
    }
}
