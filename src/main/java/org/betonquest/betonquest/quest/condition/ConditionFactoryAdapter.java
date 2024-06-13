package org.betonquest.betonquest.quest.condition;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.QuestFactory;
import org.betonquest.betonquest.api.quest.condition.Condition;
import org.betonquest.betonquest.api.quest.condition.ConditionFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerlessConditionFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.quest.QuestTypeAdapter;

/**
 * Factory adapter for that will provide both {@link PlayerCondition} and {@link PlayerlessCondition} implementations
 * from the supplied {@link ConditionFactory}.
 */
public class ConditionFactoryAdapter extends QuestTypeAdapter<Condition, PlayerCondition, PlayerlessCondition> implements PlayerConditionFactory, PlayerlessConditionFactory {
    /**
     * Create a new ConditionFactoryAdapter to create {@link PlayerCondition}s and {@link PlayerlessCondition}s from it.
     *
     * @param conditionFactory the factory used to parse the instruction.
     */
    public ConditionFactoryAdapter(final QuestFactory<Condition> conditionFactory) {
        super(conditionFactory);
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws InstructionParseException {
        return factory.parse(instruction);
    }

    @Override
    public PlayerlessCondition parsePlayerless(final Instruction instruction) throws InstructionParseException {
        return factory.parse(instruction);
    }
}
