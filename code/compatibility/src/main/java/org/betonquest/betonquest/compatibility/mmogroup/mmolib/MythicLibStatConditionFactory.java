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
        final Variable<String> statName = instruction.get(instruction.getParsers().string());
        final Variable<Number> targetLevel = instruction.get(instruction.getParsers().number());
        final boolean equal = instruction.hasArgument("equal");
        return new MythicLibStatCondition(statName, targetLevel, equal);
    }
}
