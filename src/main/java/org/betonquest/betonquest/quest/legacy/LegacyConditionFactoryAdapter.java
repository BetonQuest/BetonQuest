package org.betonquest.betonquest.quest.legacy;

import org.betonquest.betonquest.api.quest.PlayerQuestFactory;
import org.betonquest.betonquest.api.quest.PlayerlessQuestFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.instruction.Instruction;
import org.jetbrains.annotations.Nullable;

/**
 * Adapter to let {@link PlayerConditionFactory ConditionFactories} create
 * {@link org.betonquest.betonquest.api.Condition Legacy Conditions}s from the
 * {@link PlayerCondition}s and {@link PlayerlessCondition}s they create.
 */
public class LegacyConditionFactoryAdapter extends LegacyFactoryAdapter<PlayerCondition, PlayerlessCondition, org.betonquest.betonquest.api.Condition> {
    /**
     * Create the factory from an {@link PlayerConditionFactory}.
     * <p>
     * When no player factory is given the playerless factory is required.
     *
     * @param playerFactory     the player factory to use
     * @param playerlessFactory the playerless factory to use
     */
    public LegacyConditionFactoryAdapter(@Nullable final PlayerQuestFactory<PlayerCondition> playerFactory,
                                         @Nullable final PlayerlessQuestFactory<PlayerlessCondition> playerlessFactory) {
        super(playerFactory, playerlessFactory);
    }

    @Override
    protected org.betonquest.betonquest.api.Condition getAdapter(final Instruction instruction, @Nullable final PlayerCondition playerType,
                                                                 @Nullable final PlayerlessCondition playerlessType) {
        return new LegacyConditionAdapter(instruction, playerType, playerlessType);
    }
}
