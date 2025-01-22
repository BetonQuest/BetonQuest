package org.betonquest.betonquest.quest.registry.type;

import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.PlayerQuestFactory;
import org.betonquest.betonquest.api.quest.PlayerlessQuestFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.quest.legacy.FromClassLegacyTypeFactory;
import org.betonquest.betonquest.quest.legacy.LegacyConditionFactoryAdapter;
import org.betonquest.betonquest.quest.legacy.LegacyTypeFactory;
import org.jetbrains.annotations.Nullable;

/**
 * Stores the condition types that can be used in BetonQuest.
 */
public class ConditionTypeRegistry extends QuestTypeRegistry<PlayerCondition, PlayerlessCondition, Condition> {
    /**
     * Create a new condition type registry.
     *
     * @param log           the logger that will be used for logging
     * @param loggerFactory the logger factory to create a new logger for the legacy quest type factory created
     */
    public ConditionTypeRegistry(final BetonQuestLogger log, final BetonQuestLoggerFactory loggerFactory) {
        super(log, loggerFactory, "condition");
    }

    @Override
    @Deprecated
    protected LegacyTypeFactory<Condition> getFromClassLegacyTypeFactory(
            final BetonQuestLogger log, final Class<? extends Condition> conditionClass) {
        return new FromClassLegacyTypeFactory<>(log, conditionClass, "condition");
    }

    @Override
    protected LegacyTypeFactory<Condition> getLegacyFactoryAdapter(
            @Nullable final PlayerQuestFactory<PlayerCondition> playerFactory,
            @Nullable final PlayerlessQuestFactory<PlayerlessCondition> playerlessFactory) {
        return new LegacyConditionFactoryAdapter(playerFactory, playerlessFactory);
    }
}
