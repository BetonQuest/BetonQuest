package org.betonquest.betonquest.kernel.registry.quest;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.quest.PlayerQuestFactory;
import org.betonquest.betonquest.api.quest.PlayerlessQuestFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.kernel.processor.adapter.ConditionAdapter;
import org.betonquest.betonquest.kernel.processor.adapter.ConditionAdapterFactory;
import org.betonquest.betonquest.kernel.registry.QuestTypeRegistry;
import org.betonquest.betonquest.kernel.registry.TypeFactory;
import org.jetbrains.annotations.Nullable;

/**
 * Stores the condition types that can be used in BetonQuest.
 */
public class ConditionTypeRegistry extends QuestTypeRegistry<PlayerCondition, PlayerlessCondition, ConditionAdapter> {
    /**
     * Create a new condition type registry.
     *
     * @param log the logger that will be used for logging
     */
    public ConditionTypeRegistry(final BetonQuestLogger log) {
        super(log, "condition");
    }

    @Override
    protected TypeFactory<ConditionAdapter> getFactoryAdapter(
            @Nullable final PlayerQuestFactory<PlayerCondition> playerFactory,
            @Nullable final PlayerlessQuestFactory<PlayerlessCondition> playerlessFactory) {
        return new ConditionAdapterFactory(playerFactory, playerlessFactory);
    }
}
