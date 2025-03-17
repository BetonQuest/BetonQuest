package org.betonquest.betonquest.kernel.processor.adapter;

import org.betonquest.betonquest.api.quest.PlayerQuestFactory;
import org.betonquest.betonquest.api.quest.PlayerlessQuestFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.variable.PlayerVariable;
import org.betonquest.betonquest.api.quest.variable.PlayerlessVariable;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.kernel.registry.TypeFactory;
import org.jetbrains.annotations.Nullable;

/**
 * Wrapper for factories creating variables.
 */
public class VariableAdapterFactory extends QuestAdapterFactory
        <PlayerVariable, PlayerlessVariable, VariableAdapter> implements TypeFactory<VariableAdapter> {

    /**
     * Create a new adapter factory from {@link org.betonquest.betonquest.api.quest QuestFactories} for
     * {@link org.betonquest.betonquest.api.quest.variable Variables}.
     *
     * @param playerFactory     the player factory to use
     * @param playerlessFactory the playerless factory to use
     */
    public VariableAdapterFactory(@Nullable final PlayerQuestFactory<PlayerVariable> playerFactory, @Nullable final PlayerlessQuestFactory<PlayerlessVariable> playerlessFactory) {
        super(playerFactory, playerlessFactory);
    }

    @Override
    protected VariableAdapter getAdapter(final Instruction instruction, @Nullable final PlayerVariable playerType, @Nullable final PlayerlessVariable playerlessType) throws QuestException {
        return new VariableAdapter(instruction, playerType, playerlessType);
    }
}
