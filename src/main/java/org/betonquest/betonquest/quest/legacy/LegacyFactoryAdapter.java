package org.betonquest.betonquest.quest.legacy;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.OnlinePlayerQuestFactory;
import org.betonquest.betonquest.api.quest.PlayerQuestFactory;
import org.betonquest.betonquest.api.quest.PlayerlessQuestFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.jetbrains.annotations.Nullable;

/**
 * Adapter to let {@link PlayerQuestFactory QuestFactories} create {@link L}s
 * from the {@link P}s, {@link S}' and {@link O}s they create.
 *
 * @param <P> player quest type
 * @param <S> playerless quest type
 * @param <O> online player quest type
 * @param <L> legacy quest type
 */
public abstract class LegacyFactoryAdapter<P, S, O, L> implements LegacyTypeFactory<L> {
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
     * The online player type factory to be adapted.
     */
    @Nullable
    private final OnlinePlayerQuestFactory<O> onlinePlayerFactory;

    /**
     * Create the factory from a {@link PlayerQuestFactory} and/or {@link PlayerlessQuestFactory}.
     * <p>
     * When no player factory is given the playerless factory is required.
     *
     * @param playerFactory       the player factory to use
     * @param playerlessFactory   the playerless factory to use
     * @param onlinePlayerFactory the online player factory to use
     * @throws IllegalArgumentException when no factory is given
     */
    public LegacyFactoryAdapter(@Nullable final PlayerQuestFactory<P> playerFactory,
                                @Nullable final PlayerlessQuestFactory<S> playerlessFactory,
                                @Nullable final OnlinePlayerQuestFactory<O> onlinePlayerFactory) {
        if (playerFactory == null && playerlessFactory == null && onlinePlayerFactory == null) {
            throw new IllegalArgumentException("One type factory must be present!");
        }
        this.playerFactory = playerFactory;
        this.playerlessFactory = playerlessFactory;
        this.onlinePlayerFactory = onlinePlayerFactory;
    }

    @Override
    public L parseInstruction(final Instruction instruction) throws InstructionParseException {
        final P playerType = playerFactory == null ? null : playerFactory.parsePlayer(instruction.copy());
        final S playerlessType = playerlessFactory == null ? null : playerlessFactory.parsePlayerless(instruction.copy());
        final O onlinePlayerType = onlinePlayerFactory == null ? null : onlinePlayerFactory.parseOnlinePlayer(instruction.copy());
        return getAdapter(instruction, playerType, playerlessType, onlinePlayerType);
    }

    /**
     * Creates a new adapter from the new API to the old system.
     * <p>
     * When no player factory is given the playerless factory is required.
     *
     * @param instruction      the instruction to store in the adapter, satisfying old needs
     * @param playerType       the player type to adapt
     * @param playerlessType   the playerless type to adapt
     * @param onlinePlayerType the online player type to adapt
     * @return the new adapter
     * @throws InstructionParseException when the instruction cannot be parsed
     * @throws IllegalArgumentException  when no factory is given
     */
    protected abstract L getAdapter(Instruction instruction, @Nullable P playerType, @Nullable S playerlessType,
                                    @Nullable O onlinePlayerType) throws InstructionParseException;
}
