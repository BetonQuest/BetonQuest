package org.betonquest.betonquest.quest.legacy;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.QuestFactory;
import org.betonquest.betonquest.api.quest.StaticQuestFactory;
import org.betonquest.betonquest.api.quest.condition.Condition;
import org.betonquest.betonquest.api.quest.condition.ConditionFactory;
import org.betonquest.betonquest.api.quest.condition.StaticCondition;
import org.jetbrains.annotations.Nullable;

/**
 * Adapter to let {@link ConditionFactory ConditionFactories} create
 * {@link org.betonquest.betonquest.api.Condition Legacy Conditions}s from the
 * {@link Condition Condition}s and {@link StaticCondition StaticCondition}s they create.
 */
public class LegacyConditionFactoryAdapter extends LegacyFactoryAdapter<Condition, StaticCondition, org.betonquest.betonquest.api.Condition> {
    /**
     * Create the factory from an {@link ConditionFactory}.
     * <p>
     * When no normal factory is given the static factory is required.
     *
     * @param factory       the factory to use
     * @param staticFactory static event factory to use
     */
    public LegacyConditionFactoryAdapter(@Nullable final QuestFactory<Condition> factory, @Nullable final StaticQuestFactory<StaticCondition> staticFactory) {
        super(factory, staticFactory);
    }

    @Override
    protected org.betonquest.betonquest.api.Condition getAdapter(final Instruction instruction, @Nullable final Condition type, @Nullable final StaticCondition staticType) {
        return new LegacyConditionAdapter(instruction, type, staticType);
    }
}
