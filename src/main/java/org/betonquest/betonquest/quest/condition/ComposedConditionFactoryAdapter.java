package org.betonquest.betonquest.quest.condition;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.ComposedQuestFactory;
import org.betonquest.betonquest.api.quest.ComposedQuestTypeAdapter;
import org.betonquest.betonquest.api.quest.condition.ComposedCondition;
import org.betonquest.betonquest.api.quest.condition.ComposedConditionFactory;
import org.betonquest.betonquest.api.quest.condition.Condition;
import org.betonquest.betonquest.api.quest.condition.ConditionFactory;
import org.betonquest.betonquest.api.quest.condition.StaticCondition;
import org.betonquest.betonquest.api.quest.condition.StaticConditionFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;

/**
 * Factory adapter for that will provide both {@link Condition} and {@link StaticCondition} implementations
 * from the supplied {@link ComposedConditionFactory}.
 */
public class ComposedConditionFactoryAdapter extends ComposedQuestTypeAdapter<ComposedCondition, Condition, StaticCondition> implements ConditionFactory, StaticConditionFactory {
    /**
     * Create a new ComposedConditionFactoryAdapter to create {@link Condition}s and {@link StaticCondition}s from it.
     *
     * @param composedConditionFactory the factory used to parse the instruction.
     */
    public ComposedConditionFactoryAdapter(final ComposedQuestFactory<ComposedCondition> composedConditionFactory) {
        super(composedConditionFactory);
    }

    @Override
    public Condition parse(final Instruction instruction) throws InstructionParseException {
        return composedFactory.parseComposed(instruction);
    }

    @Override
    public StaticCondition parseStatic(final Instruction instruction) throws InstructionParseException {
        return composedFactory.parseComposed(instruction);
    }
}
