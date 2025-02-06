package org.betonquest.betonquest.compatibility.brewery.condition;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.condition.PrimaryServerThreadPlayerCondition;

/**
 * Factory to create {@link DrunkCondition}s from {@link Instruction}s.
 */
public class DrunkConditionFactory implements PlayerConditionFactory {
    /**
     * Data used for primary server access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create a new Factory to create Drunk Conditions.
     *
     * @param data the data used for primary server access.
     */
    public DrunkConditionFactory(final PrimaryServerThreadData data) {
        this.data = data;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final VariableNumber drunkVar = instruction.get(VariableNumber::new);
        return new PrimaryServerThreadPlayerCondition(new DrunkCondition(drunkVar), data);
    }
}
