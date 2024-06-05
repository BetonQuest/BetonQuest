package org.betonquest.betonquest.quest;

import org.betonquest.betonquest.api.quest.ComposedQuestFactory;
import org.betonquest.betonquest.api.quest.QuestFactory;
import org.betonquest.betonquest.api.quest.StaticQuestFactory;

/**
 * Factory adapter for that will provide both {@link T} and {@link S Static T} implementations
 * from the supplied {@link ComposedQuestFactory}.
 *
 * @param <C> the composed type extending {@link T} and {@link S}
 * @param <T> the quest type
 * @param <S> the static variant of {@link T}
 */
public abstract class ComposedQuestTypeAdapter<C, T, S> implements QuestFactory<T>, StaticQuestFactory<S> {
    /**
     * Composed factory used to create new {@link T} and static T.
     */
    protected final ComposedQuestFactory<C> composedFactory;

    /**
     * Create a new ComposedEventFactoryAdapter to create {@link T}s and {@link S}' from it.
     *
     * @param composedFactory the factory used to parse the instruction.
     */
    public ComposedQuestTypeAdapter(final ComposedQuestFactory<C> composedFactory) {
        this.composedFactory = composedFactory;
    }
}
