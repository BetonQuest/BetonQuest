package org.betonquest.betonquest.compatibility.mmogroup.mmolib;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.DefaultInstruction;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.thread.PrimaryServerThreadPlayerCondition;

/**
 * Factory to create {@link MythicLibStatCondition}s from {@link DefaultInstruction}s.
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
    public PlayerCondition parsePlayer(final DefaultInstruction instruction) throws QuestException {
        final Variable<String> statName = instruction.get(Argument.STRING);
        final Variable<Number> targetLevel = instruction.get(Argument.NUMBER);
        final boolean equal = instruction.hasArgument("equal");
        return new PrimaryServerThreadPlayerCondition(new MythicLibStatCondition(statName, targetLevel, equal), data);
    }
}
