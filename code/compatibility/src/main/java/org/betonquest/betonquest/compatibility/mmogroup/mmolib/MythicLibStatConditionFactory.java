package org.betonquest.betonquest.compatibility.mmogroup.mmolib;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.Instruction;
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
        final Argument<String> statName = instruction.string().get();
        final Argument<Number> targetLevel = instruction.number().get();
        final FlagArgument<Boolean> equal = instruction.bool().getFlag("equal", true);
        return new MythicLibStatCondition(statName, targetLevel, equal);
    }
}
