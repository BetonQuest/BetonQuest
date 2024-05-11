package org.betonquest.betonquest.quest.condition.legacy;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.QuestFactory;
import org.betonquest.betonquest.api.quest.StaticQuestFactory;
import org.betonquest.betonquest.api.quest.condition.Condition;
import org.betonquest.betonquest.api.quest.condition.ConditionFactory;
import org.betonquest.betonquest.api.quest.condition.StaticCondition;
import org.betonquest.betonquest.exceptions.InstructionParseException;

/**
 * Adapter to let {@link ConditionFactory ConditionFactories} create
 * {@link org.betonquest.betonquest.api.Condition Legacy Conditions}s from the
 * {@link Condition Condition}s and {@link StaticCondition StaticCondition}s they create.
 */
public class LegacyConditionFactoryAdapter implements LegacyConditionFactory {
    /**
     * The condition factory to be adapted.
     */
    private final QuestFactory<Condition> factory;

    /**
     * The static condition factory to be adapted.
     */
    private final StaticQuestFactory<StaticCondition> staticFactory;

    /**
     * Create the factory from an {@link ConditionFactory}.
     *
     * @param factory       the factory to use
     * @param staticFactory static event factory to use
     */
    public LegacyConditionFactoryAdapter(final QuestFactory<Condition> factory, final StaticQuestFactory<StaticCondition> staticFactory) {
        this.factory = factory;
        this.staticFactory = staticFactory;
    }

    @Override
    public LegacyConditionAdapter parseConditionInstruction(final Instruction instruction) throws InstructionParseException {
        final Condition condition = factory.parse(instruction.copy());
        final StaticCondition staticCondition = staticFactory.parseStatic(instruction.copy());
        return new LegacyConditionAdapter(instruction, condition, staticCondition);
    }
}
