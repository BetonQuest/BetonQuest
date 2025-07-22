package org.betonquest.betonquest.compatibility.mmogroup.mmolib;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.condition.PrimaryServerThreadPlayerCondition;

/**
 * Factory to create {@link MythicLibStatCondition}s from {@link Instruction}s.
 */
public class MythicLibStatConditionFactory implements PlayerConditionFactory {

    /**
     * Data for primary server thread access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create a new factory for the Mythic Lib Condition.
     *
     * @param data the data for primary server thread access
     */
    public MythicLibStatConditionFactory(final PrimaryServerThreadData data) {
        this.data = data;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final String statName = instruction.next();
        final Variable<Number> targetLevel = instruction.get(Argument.NUMBER);
        final boolean equal = instruction.hasArgument("equal");
        return new PrimaryServerThreadPlayerCondition(new MythicLibStatCondition(statName, targetLevel, equal), data);
    }
}
