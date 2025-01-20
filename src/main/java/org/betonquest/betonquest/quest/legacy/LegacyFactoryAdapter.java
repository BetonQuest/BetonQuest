package org.betonquest.betonquest.quest.legacy;

import org.betonquest.betonquest.api.quest.PlayerQuestFactory;
import org.betonquest.betonquest.api.quest.PlayerlessQuestFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.Instruction;
import org.jetbrains.annotations.Nullable;

/**
 * Adapter to let {@link PlayerQuestFactory QuestFactories} create {@link L}s
 * from the {@link P}s and {@link S}' they create.
 *
 * @param <P> player quest type
 * @param <S> playerless quest type
 * @param <L> legacy quest type
 */
public abstract class LegacyFactoryAdapter<P, S, L> implements LegacyTypeFactory<L> {
    /**
     * The player type factory to be adapted.
     */
    @Nullable
    private final PlayerQuestFactory<P> playerFactory;

    /**
     * The playerless type factory to be adapted.
     */
    @Nullable
    private final PlayerlessQuestFactory<S> playerlessFactory;

    /**
     * Create the factory from a {@link PlayerQuestFactory} and/or {@link PlayerlessQuestFactory}.
     * <p>
     * When no player factory is given the playerless factory is required.
     *
     * @param playerFactory     the player factory to use
     * @param playerlessFactory the playerless factory to use
     */
    public LegacyFactoryAdapter(@Nullable final PlayerQuestFactory<P> playerFactory,
                                @Nullable final PlayerlessQuestFactory<S> playerlessFactory) {
        if (playerFactory == null && playerlessFactory == null) {
            throw new IllegalArgumentException("Either the player or playerless factory must be present!");
        }
        this.playerFactory = playerFactory;
        this.playerlessFactory = playerlessFactory;
    }

    @Override
    public L parseInstruction(final Instruction instruction) throws QuestException {
        final P playerType = playerFactory == null ? null : playerFactory.parsePlayer(instruction.copy());
        final S playerlessType = playerlessFactory == null ? null : playerlessFactory.parsePlayerless(instruction.copy());
        return getAdapter(instruction, playerType, playerlessType);
    }

    /**
     * Creates a new adapter from the new API to the old system.
     * <p>
     * When no player factory is given the playerless factory is required.
     *
     * @param instruction    the instruction to store in the adapter, satisfying old needs
     * @param playerType     the player type to adapt
     * @param playerlessType the playerless type to adapt
     * @return the new adapter
     * @throws QuestException when the instruction cannot be parsed
     */
    protected abstract L getAdapter(Instruction instruction, @Nullable P playerType, @Nullable S playerlessType) throws QuestException;
}
