package org.betonquest.betonquest.kernel.processor.adapter;

import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.PlayerQuestFactory;
import org.betonquest.betonquest.api.quest.PlayerlessQuestFactory;
import org.betonquest.betonquest.api.quest.placeholder.PlayerPlaceholder;
import org.betonquest.betonquest.api.quest.placeholder.PlayerlessPlaceholder;
import org.jetbrains.annotations.Nullable;

/**
 * Wrapper for factories creating placeholders.
 */
public class PlaceholderAdapterFactory extends QuestAdapterFactory<PlayerPlaceholder, PlayerlessPlaceholder, PlaceholderAdapter> {

    /**
     * Create a new adapter factory from {@link org.betonquest.betonquest.api.quest QuestFactories} for
     * {@link org.betonquest.betonquest.api.quest.placeholder Placeholders}.
     *
     * @param playerFactory     the player factory to use
     * @param playerlessFactory the playerless factory to use
     * @throws IllegalArgumentException if no factory is given
     */
    public PlaceholderAdapterFactory(@Nullable final PlayerQuestFactory<PlayerPlaceholder> playerFactory,
                                     @Nullable final PlayerlessQuestFactory<PlayerlessPlaceholder> playerlessFactory) {
        super(playerFactory, playerlessFactory);
    }

    @Override
    protected PlaceholderAdapter getAdapter(final Instruction instruction,
                                            @Nullable final PlayerPlaceholder playerType,
                                            @Nullable final PlayerlessPlaceholder playerlessType) {
        return new PlaceholderAdapter(instruction, playerType, playerlessType);
    }
}
