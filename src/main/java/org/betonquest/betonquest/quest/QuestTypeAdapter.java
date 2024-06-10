package org.betonquest.betonquest.quest;

import org.betonquest.betonquest.api.quest.PlayerQuestFactory;
import org.betonquest.betonquest.api.quest.PlayerlessQuestFactory;
import org.betonquest.betonquest.api.quest.QuestFactory;

/**
 * Factory adapter for that will provide both {@link T} and {@link S} implementations
 * from the supplied {@link QuestFactory}.
 *
 * @param <C> the type extending {@link T} and {@link S}
 * @param <T> the player variant of the quest type
 * @param <S> the playerless variant of the quest type
 */
public abstract class QuestTypeAdapter<C, T, S> implements PlayerQuestFactory<T>, PlayerlessQuestFactory<S> {
    /**
     * Factory used to create new {@link T} and {@link S}.
     */
    protected final QuestFactory<C> factory;

    /**
     * Create a new FactoryAdapter to create {@link T}s and {@link S}' from it.
     *
     * @param factory the factory used to parse the instruction.
     */
    public QuestTypeAdapter(final QuestFactory<C> factory) {
        this.factory = factory;
    }
}
