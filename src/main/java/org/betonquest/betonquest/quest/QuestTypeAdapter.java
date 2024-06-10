package org.betonquest.betonquest.quest;

import org.betonquest.betonquest.api.quest.PlayerQuestFactory;
import org.betonquest.betonquest.api.quest.PlayerlessQuestFactory;
import org.betonquest.betonquest.api.quest.QuestFactory;

/**
 * Factory adapter for that will provide both {@link P} and {@link S} implementations
 * from the supplied {@link QuestFactory}.
 *
 * @param <T> the type extending {@link P} and {@link S}
 * @param <P> the player variant of the quest type
 * @param <S> the playerless variant of the quest type
 */
public abstract class QuestTypeAdapter<T, P, S> implements PlayerQuestFactory<P>, PlayerlessQuestFactory<S> {
    /**
     * Factory used to create new {@link P} and {@link S}.
     */
    protected final QuestFactory<T> factory;

    /**
     * Create a new FactoryAdapter to create {@link P}s and {@link S}' from it.
     *
     * @param factory the factory used to parse the instruction.
     */
    public QuestTypeAdapter(final QuestFactory<T> factory) {
        this.factory = factory;
    }
}
