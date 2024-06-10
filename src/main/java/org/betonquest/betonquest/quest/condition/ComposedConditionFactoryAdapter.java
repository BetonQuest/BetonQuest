package org.betonquest.betonquest.quest.condition;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.ComposedQuestFactory;
import org.betonquest.betonquest.api.quest.condition.ComposedCondition;
import org.betonquest.betonquest.api.quest.condition.ComposedConditionFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.StaticCondition;
import org.betonquest.betonquest.api.quest.condition.StaticConditionFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.quest.ComposedQuestTypeAdapter;

/**
 * Factory adapter for that will provide both {@link PlayerCondition} and {@link StaticCondition} implementations
 * from the supplied {@link ComposedConditionFactory}.
 */
public class ComposedConditionFactoryAdapter extends ComposedQuestTypeAdapter<ComposedCondition, PlayerCondition, StaticCondition> implements PlayerConditionFactory, StaticConditionFactory {
    /**
     * Create a new ComposedConditionFactoryAdapter to create {@link PlayerCondition}s and {@link StaticCondition}s from it.
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
    public StaticCondition parseStatic(final Instruction instruction) throws InstructionParseException {
        return composedFactory.parseComposed(instruction);
    }
}
