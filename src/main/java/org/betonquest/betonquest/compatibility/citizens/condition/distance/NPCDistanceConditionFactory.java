package org.betonquest.betonquest.compatibility.citizens.condition.distance;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.condition.PrimaryServerThreadPlayerCondition;

/**
 * Factory to create {@link NPCDistanceCondition}s from {@link Instruction}s.
 */
public class NPCDistanceConditionFactory implements PlayerConditionFactory {
    /**
     * Data used for primary server thread access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create a new factory for NPC Distance Conditions.
     *
     * @param data the data for primary server thread access
     */
    public NPCDistanceConditionFactory(final PrimaryServerThreadData data) {
        this.data = data;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws InstructionParseException {
        final int npcId = instruction.getInt();
        if (npcId < 0) {
            throw new InstructionParseException("NPC ID cannot be less than 0");
        }
        final VariableNumber distance = instruction.getVarNum();
        // TODO online player require
        return new PrimaryServerThreadPlayerCondition(new NPCDistanceCondition(npcId, distance), data);
    }
}
