package org.betonquest.betonquest.quest.condition;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.ComposedQuestFactory;
import org.betonquest.betonquest.api.quest.condition.ComposedCondition;
import org.betonquest.betonquest.api.quest.condition.ComposedConditionFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerlessConditionFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.quest.ComposedQuestTypeAdapter;

/**
 * Factory adapter for that will provide both {@link PlayerCondition} and {@link PlayerlessCondition} implementations
 * from the supplied {@link ComposedConditionFactory}.
 */
public class ComposedConditionFactoryAdapter extends ComposedQuestTypeAdapter<ComposedCondition, PlayerCondition, PlayerlessCondition> implements PlayerConditionFactory, PlayerlessConditionFactory {
    /**
     * Create a new ComposedConditionFactoryAdapter to create {@link PlayerCondition}s and {@link PlayerlessCondition}s from it.
     *
     * @param composedConditionFactory the factory used to parse the instruction.
     */
    public ComposedConditionFactoryAdapter(final ComposedQuestFactory<ComposedCondition> composedConditionFactory) {
        super(composedConditionFactory);
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws InstructionParseException {
        return composedFactory.parseComposed(instruction);
    }

    @Override
    public PlayerlessCondition parsePlayerless(final Instruction instruction) throws InstructionParseException {
        return composedFactory.parseComposed(instruction);
    }
}
