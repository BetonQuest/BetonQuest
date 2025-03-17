package org.betonquest.betonquest.kernel.processor.adapter;

import org.betonquest.betonquest.api.quest.PlayerQuestFactory;
import org.betonquest.betonquest.api.quest.PlayerlessQuestFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.Instruction;
import org.jetbrains.annotations.Nullable;

/**
 * Factory to create Adapter from QuestFactories with the {@link P}s and {@link S}' they create.
 *
 * @param <P> player quest type
 * @param <S> playerless quest type
 * @param <W> adapter for created types
 */
public abstract class QuestAdapterFactory<P, S, W> {
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
     * Create a new adapter factory from {@link org.betonquest.betonquest.api.quest QuestFactories}.
     *
     * @param playerFactory     the player factory to use
     * @param playerlessFactory the playerless factory to use
     * @throws IllegalArgumentException if no factory is given
     */
    public QuestAdapterFactory(@Nullable final PlayerQuestFactory<P> playerFactory,
                               @Nullable final PlayerlessQuestFactory<S> playerlessFactory) {
        if (playerFactory == null && playerlessFactory == null) {
            throw new IllegalArgumentException("Either the player or playerless factory must be present!");
        }
        this.playerFactory = playerFactory;
        this.playerlessFactory = playerlessFactory;
    }

    /**
     * Passes the instruction to the factories to get new types.
     *
     * @param instruction the instruction to parse
     * @return created wrapped types
     * @throws QuestException if the instruction cannot be parsed
     */
    public W parseInstruction(final Instruction instruction) throws QuestException {
        final P playerType = playerFactory == null ? null : playerFactory.parsePlayer(instruction.copy());
        final S playerlessType = playerlessFactory == null ? null : playerlessFactory.parsePlayerless(instruction.copy());
        return getAdapter(instruction, playerType, playerlessType);
    }

    /**
     * Creates a new Adapter for execution logic.
     *
     * @param instruction    the instruction to store in the adapter, satisfying old needs
     * @param playerType     the player type to adapt
     * @param playerlessType the playerless type to adapt
     * @return the new adapter
     * @throws QuestException           when the instruction cannot be parsed
     * @throws IllegalArgumentException if there is no type provided
     */
    protected abstract W getAdapter(Instruction instruction, @Nullable P playerType, @Nullable S playerlessType) throws QuestException;
}
