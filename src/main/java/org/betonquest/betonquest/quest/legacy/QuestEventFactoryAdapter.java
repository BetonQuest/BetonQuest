package org.betonquest.betonquest.quest.legacy;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.api.quest.PlayerQuestFactory;
import org.betonquest.betonquest.api.quest.StaticQuestFactory;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.api.quest.event.StaticEvent;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.jetbrains.annotations.Nullable;

/**
 * Adapter to let {@link EventFactory EventFactories} create {@link QuestEvent}s from the {@link Event}s and
 * {@link StaticEvent}s they create.
 */
public class QuestEventFactoryAdapter extends LegacyFactoryAdapter<Event, StaticEvent, QuestEvent> {
    /**
     * Create the factory from an {@link EventFactory}.
     * <p>
     * When no normal factory is given the static factory is required.
     *
     * @param factory       event factory to use
     * @param staticFactory static event factory to use
     */
    public QuestEventFactoryAdapter(@Nullable final PlayerQuestFactory<Event> factory, @Nullable final StaticQuestFactory<StaticEvent> staticFactory) {
        super(factory, staticFactory);
    }

    @Override
    protected QuestEvent getAdapter(final Instruction instruction, @Nullable final Event type, @Nullable final StaticEvent staticType)
            throws InstructionParseException {
        return new QuestEventAdapter(instruction, type, staticType);
    }
}
