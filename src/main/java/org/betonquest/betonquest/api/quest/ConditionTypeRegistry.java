package org.betonquest.betonquest.api.quest;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.condition.ComposedCondition;
import org.betonquest.betonquest.api.quest.condition.Condition;
import org.betonquest.betonquest.api.quest.condition.StaticCondition;
import org.betonquest.betonquest.quest.condition.ComposedConditionFactoryAdapter;
import org.betonquest.betonquest.quest.condition.legacy.FromClassLegacyConditionFactory;
import org.betonquest.betonquest.quest.condition.legacy.LegacyConditionFactory;
import org.betonquest.betonquest.quest.condition.legacy.LegacyConditionFactoryAdapter;
import org.jetbrains.annotations.Nullable;

/**
 * Stores the condition types that can be used in BetonQuest.
 */
public class ConditionTypeRegistry extends QuestTypeRegistry<Condition, StaticCondition, ComposedCondition, org.betonquest.betonquest.api.Condition, LegacyConditionFactory> {
    /**
     * Create a new event type registry.
     *
     * @param log           the logger that will be used for logging
     * @param loggerFactory the logger factory to create a new logger for the legacy quest type factory created
     */
    public ConditionTypeRegistry(final BetonQuestLogger log, final BetonQuestLoggerFactory loggerFactory) {
        super(log, loggerFactory, "condition");
    }

    @Override
    protected LegacyConditionFactory getFromClassLegacyTypeFactory(final BetonQuestLogger log, final Class<? extends org.betonquest.betonquest.api.Condition> conditionClass) {
        return new FromClassLegacyConditionFactory<>(log, conditionClass);
    }

    @Override
    protected LegacyConditionFactory getLegacyFactoryAdapter(@Nullable final QuestFactory<Condition> eventFactory,
                                                             @Nullable final StaticQuestFactory<StaticCondition> staticEventFactory) {
        return new LegacyConditionFactoryAdapter(eventFactory, staticEventFactory);
    }

    @Override
    protected ComposedQuestTypeAdapter<ComposedCondition, Condition, StaticCondition> getComposedAdapter(final ComposedQuestFactory<ComposedCondition> composedFactory) {
        return new ComposedConditionFactoryAdapter(composedFactory);
    }
}
