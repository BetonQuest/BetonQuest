package org.betonquest.betonquest.compatibility.mmogroup.mmolib;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;

/**
 * Factory to create {@link MythicLibStatCondition}s from {@link Instruction}s.
 */
public class MythicLibStatConditionFactory implements PlayerConditionFactory {

    /**
     * Create a new factory for the Mythic Lib Condition.
     */
    public MythicLibStatConditionFactory() {
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Variable<String> statName = instruction.string().get();
        final Variable<Number> targetLevel = instruction.number().get();
        final boolean equal = instruction.hasArgument("equal");
        return new MythicLibStatCondition(statName, targetLevel, equal);
    }
}
