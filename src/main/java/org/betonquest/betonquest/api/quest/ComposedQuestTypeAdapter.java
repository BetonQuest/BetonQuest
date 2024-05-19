package org.betonquest.betonquest.api.quest;

import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.StaticEvent;

/**
 * Factory adapter for that will provide both {@link T} and {@link S Static T} implementations
 * from the supplied {@link ComposedQuestFactory}.
 *
 * @param <H> the composed type extending {@link T} and {@link S}
 * @param <T> the quest type
 * @param <S> the static variant of {@link T}
 */
public abstract class ComposedQuestTypeAdapter<H, T, S> implements QuestFactory<T>, StaticQuestFactory<S> {
    /**
     * Composed factory used to create new {@link T} and static T.
     */
    protected final ComposedQuestFactory<H> composedFactory;

    /**
     * Create a new ComposedEventFactoryAdapter to create {@link Event}s and {@link StaticEvent}s from it.
     *
     * @param composedFactory the factory used to parse the instruction.
     */
    public ComposedQuestTypeAdapter(final ComposedQuestFactory<H> composedFactory) {
        this.composedFactory = composedFactory;
    }
}
