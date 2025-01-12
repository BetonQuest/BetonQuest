package org.betonquest.betonquest.compatibility.citizens.condition.location;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerlessConditionFactory;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableConditionAdapter;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.condition.PrimaryServerThreadPlayerCondition;
import org.betonquest.betonquest.quest.condition.PrimaryServerThreadPlayerlessCondition;

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
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        return new PrimaryServerThreadPlayerCondition(parseNpcLocationCondition(instruction), data);
    }

    @Override
    public PlayerlessCondition parsePlayerless(final Instruction instruction) throws QuestException {
        return new PrimaryServerThreadPlayerlessCondition(parseNpcLocationCondition(instruction), data);
    }

    private NullableConditionAdapter parseNpcLocationCondition(final Instruction instruction) throws QuestException {
        final int npcId = instruction.getInt();
        if (npcId < 0) {
            throw new QuestException("NPC ID cannot be less than 0");
        }
        final VariableLocation location = instruction.getLocation();
        final VariableNumber radius = instruction.getVarNum();
        return new NullableConditionAdapter(new NPCLocationCondition(npcId, location, radius));
    }
}
