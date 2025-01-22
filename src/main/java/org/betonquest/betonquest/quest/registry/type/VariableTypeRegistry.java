package org.betonquest.betonquest.quest.registry.type;

import org.betonquest.betonquest.api.Variable;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.PlayerQuestFactory;
import org.betonquest.betonquest.api.quest.PlayerlessQuestFactory;
import org.betonquest.betonquest.api.quest.variable.PlayerVariable;
import org.betonquest.betonquest.api.quest.variable.PlayerlessVariable;
import org.betonquest.betonquest.quest.legacy.FromClassLegacyTypeFactory;
import org.betonquest.betonquest.quest.legacy.LegacyTypeFactory;
import org.betonquest.betonquest.quest.legacy.LegacyVariableFactoryAdapter;
import org.jetbrains.annotations.Nullable;

/**
 * Stores the variable types that can be used in BetonQuest.
 */
public class VariableTypeRegistry extends QuestTypeRegistry<PlayerVariable, PlayerlessVariable, Variable> {
    /**
     * Logger factory for creating new custom loggers.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Create a new variable type registry.
     *
     * @param log           the logger that will be used for logging
     * @param loggerFactory the logger factory to create a new logger for the legacy quest type factory created
     */
    public VariableTypeRegistry(final BetonQuestLogger log, final BetonQuestLoggerFactory loggerFactory) {
        super(log, loggerFactory, "variable");
        this.loggerFactory = loggerFactory;
    }

    @Override
    @Deprecated
    protected LegacyTypeFactory<Variable> getFromClassLegacyTypeFactory(
            final BetonQuestLogger log, final Class<? extends Variable> lClass) {
        return new FromClassLegacyTypeFactory<>(log, lClass, "variable");
    }

    @Override
    protected LegacyTypeFactory<Variable> getLegacyFactoryAdapter(
            @Nullable final PlayerQuestFactory<PlayerVariable> playerFactory,
            @Nullable final PlayerlessQuestFactory<PlayerlessVariable> playerlessFactory) {
        return new LegacyVariableFactoryAdapter(playerFactory, playerlessFactory, loggerFactory);
    }
}
