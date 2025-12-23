package org.betonquest.betonquest.compatibility.mmogroup.mmocore.condition;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;

/**
 * Factory to create {@link MMOCoreClassCondition}s from {@link Instruction}s.
 */
public class MMOCoreClassConditionFactory implements PlayerConditionFactory {

    /**
     * Create a new MMO Core Condition Factory.
     */
    public MMOCoreClassConditionFactory() {
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Variable<String> className = instruction.get(instruction.getParsers().string());
        final Variable<Number> classLevel = instruction.hasNext() ? instruction.get(instruction.getParsers().number()) : null;
        final boolean equal = instruction.hasArgument("equal");
        return new MMOCoreClassCondition(className, classLevel, equal);
    }
}
