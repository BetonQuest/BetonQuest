package org.betonquest.betonquest.compatibility.mmogroup.mmocore.condition;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.condition.PrimaryServerThreadPlayerCondition;

/**
 * Factory to create {@link MMOCoreClassCondition}s from {@link Instruction}s.
 */
public class MMOCoreClassConditionFactory implements PlayerConditionFactory {

    /**
     * Data for primary server thread access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create a new MMO Core Condition Factory.
     *
     * @param data the data for primary server thread access
     */
    public MMOCoreClassConditionFactory(final PrimaryServerThreadData data) {
        this.data = data;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Variable<String> className = instruction.get(Argument.STRING);
        final Variable<Number> classLevel = instruction.hasNext() ? instruction.get(Argument.NUMBER) : null;
        final boolean equal = instruction.hasArgument("equal");
        return new PrimaryServerThreadPlayerCondition(
                new MMOCoreClassCondition(className, classLevel, equal),
                data);
    }
}
