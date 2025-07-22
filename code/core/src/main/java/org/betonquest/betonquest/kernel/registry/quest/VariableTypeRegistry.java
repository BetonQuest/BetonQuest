package org.betonquest.betonquest.kernel.registry.quest;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.quest.PlayerQuestFactory;
import org.betonquest.betonquest.api.quest.PlayerlessQuestFactory;
import org.betonquest.betonquest.api.quest.variable.PlayerVariable;
import org.betonquest.betonquest.api.quest.variable.PlayerlessVariable;
import org.betonquest.betonquest.kernel.processor.adapter.VariableAdapter;
import org.betonquest.betonquest.kernel.processor.adapter.VariableAdapterFactory;
import org.betonquest.betonquest.kernel.registry.QuestTypeRegistry;
import org.betonquest.betonquest.kernel.registry.TypeFactory;
import org.jetbrains.annotations.Nullable;

/**
 * Stores the variable types that can be used in BetonQuest.
 */
public class VariableTypeRegistry extends QuestTypeRegistry<PlayerVariable, PlayerlessVariable, VariableAdapter> {

    /**
     * Create a new variable type registry.
     *
     * @param log the logger that will be used for logging
     */
    public VariableTypeRegistry(final BetonQuestLogger log) {
        super(log, "variable");
    }

    @Override
    protected TypeFactory<VariableAdapter> getFactoryAdapter(
            @Nullable final PlayerQuestFactory<PlayerVariable> playerFactory,
            @Nullable final PlayerlessQuestFactory<PlayerlessVariable> playerlessFactory) {
        return new VariableAdapterFactory(playerFactory, playerlessFactory);
    }
}
