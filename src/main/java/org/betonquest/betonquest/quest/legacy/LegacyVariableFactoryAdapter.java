package org.betonquest.betonquest.quest.legacy;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Variable;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.PlayerQuestFactory;
import org.betonquest.betonquest.api.quest.PlayerlessQuestFactory;
import org.betonquest.betonquest.api.quest.variable.PlayerVariable;
import org.betonquest.betonquest.api.quest.variable.PlayerlessVariable;
import org.jetbrains.annotations.Nullable;

/**
 * Adapter to let VariableFactories create {@link Variable Legacy Variables}s
 * from the {@link PlayerVariable}s and {@link PlayerlessVariable}s they create.
 */
public class LegacyVariableFactoryAdapter extends LegacyFactoryAdapter<PlayerVariable, PlayerlessVariable, Variable> {
    /**
     * The custom logger for the LegacyAdapter.
     */
    private final BetonQuestLogger logger;

    /**
     * Create the factory from an {@link PlayerQuestFactory} and/or {@link PlayerlessQuestFactory}.
     * <p>
     * When no player factory is given the playerless factory is required.
     *
     * @param playerFactory     the player factory to use
     * @param playerlessFactory the playerless factory to use
     * @param loggerFactory     the logger factory to create new custom logger with
     */
    public LegacyVariableFactoryAdapter(@Nullable final PlayerQuestFactory<PlayerVariable> playerFactory,
                                        @Nullable final PlayerlessQuestFactory<PlayerlessVariable> playerlessFactory,
                                        final BetonQuestLoggerFactory loggerFactory) {
        super(playerFactory, playerlessFactory);
        logger = loggerFactory.create(LegacyVariableAdapter.class);
    }

    @Override
    protected Variable getAdapter(final Instruction instruction, @Nullable final PlayerVariable playerType,
                                  @Nullable final PlayerlessVariable playerlessType) {
        return new LegacyVariableAdapter(instruction, playerType, playerlessType, logger);
    }
}
