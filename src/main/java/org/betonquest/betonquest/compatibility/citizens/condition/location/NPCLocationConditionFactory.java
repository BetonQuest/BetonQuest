package org.betonquest.betonquest.compatibility.citizens.condition.location;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.condition.Condition;
import org.betonquest.betonquest.api.quest.condition.ConditionFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.condition.PrimaryServerThreadCondition;

/**
 * Factory to create {@link NPCLocationCondition}s from {@link Instruction}s.
 */
public class NPCLocationConditionFactory implements ConditionFactory {
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
    public Condition parse(final Instruction instruction) throws InstructionParseException {
        final int npcId = instruction.getInt();
        if (npcId < 0) {
            throw new InstructionParseException("NPC ID cannot be less than 0");
        }
        final VariableLocation location = instruction.getLocation();
        final VariableNumber radius = instruction.getVarNum();
        return new PrimaryServerThreadCondition(new NPCLocationCondition(npcId, location, radius), data);
    }
}
