package org.betonquest.betonquest.kernel.processor.adapter;

import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.PlayerQuestFactory;
import org.betonquest.betonquest.api.quest.PlayerlessQuestFactory;
import org.betonquest.betonquest.api.quest.variable.PlayerVariable;
import org.betonquest.betonquest.api.quest.variable.PlayerlessVariable;
import org.jetbrains.annotations.Nullable;

/**
 * Wrapper for factories creating variables.
 */
public class VariableAdapterFactory extends QuestAdapterFactory<PlayerVariable, PlayerlessVariable, VariableAdapter> {

    /**
     * Create a new adapter factory from {@link org.betonquest.betonquest.api.quest QuestFactories} for
     * {@link org.betonquest.betonquest.api.quest.variable Variables}.
     *
     * @param playerFactory     the player factory to use
     * @param playerlessFactory the playerless factory to use
     * @throws IllegalArgumentException if no factory is given
     */
    public VariableAdapterFactory(@Nullable final PlayerQuestFactory<PlayerVariable> playerFactory,
                                  @Nullable final PlayerlessQuestFactory<PlayerlessVariable> playerlessFactory) {
        super(playerFactory, playerlessFactory);
    }

    @Override
    protected VariableAdapter getAdapter(final Instruction instruction,
                                         @Nullable final PlayerVariable playerType,
                                         @Nullable final PlayerlessVariable playerlessType) {
        return new VariableAdapter(instruction, playerType, playerlessType);
    }
}
