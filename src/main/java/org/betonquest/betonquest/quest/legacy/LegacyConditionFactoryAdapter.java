package org.betonquest.betonquest.quest.legacy;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.PlayerQuestFactory;
import org.betonquest.betonquest.api.quest.StaticQuestFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.StaticCondition;
import org.jetbrains.annotations.Nullable;

/**
 * Adapter to let {@link PlayerConditionFactory ConditionFactories} create
 * {@link org.betonquest.betonquest.api.Condition Legacy Conditions}s from the
 * {@link PlayerCondition}s and {@link StaticCondition}s they create.
 */
public class LegacyConditionFactoryAdapter extends LegacyFactoryAdapter<PlayerCondition, StaticCondition, org.betonquest.betonquest.api.Condition> {
    /**
     * Create the factory from an {@link PlayerConditionFactory}.
     * <p>
     * When no normal factory is given the static factory is required.
     *
     * @param factory       the factory to use
     * @param staticFactory static event factory to use
     */
    public LegacyConditionFactoryAdapter(@Nullable final PlayerQuestFactory<PlayerCondition> factory, @Nullable final StaticQuestFactory<StaticCondition> staticFactory) {
        super(factory, staticFactory);
    }

    @Override
    protected org.betonquest.betonquest.api.Condition getAdapter(final Instruction instruction, @Nullable final PlayerCondition type, @Nullable final StaticCondition staticType) {
        return new LegacyConditionAdapter(instruction, type, staticType);
    }
}
