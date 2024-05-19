package org.betonquest.betonquest.quest.event.legacy;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.api.quest.QuestFactory;
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
public class QuestEventFactoryAdapter implements QuestEventFactory {
    /**
     * The event factory to be adapted.
     */
    @Nullable
    private final QuestFactory<Event> factory;

    /**
     * The static event factory to be adapted.
     */
    @Nullable
    private final StaticQuestFactory<StaticEvent> staticFactory;

    /**
     * Create the factory from an {@link EventFactory}.
     * <p>
     * When no normal factory is given the static factory is required.
     *
     * @param factory       event factory to use
     * @param staticFactory static event factory to use
     */
    public QuestEventFactoryAdapter(@Nullable final QuestFactory<Event> factory, @Nullable final StaticQuestFactory<StaticEvent> staticFactory) {
        if (factory == null && staticFactory == null) {
            throw new IllegalArgumentException("Either the normal or static factory must be present!");
        }
        this.factory = factory;
        this.staticFactory = staticFactory;
    }

    @Override
    public QuestEventAdapter parseEventInstruction(final Instruction instruction) throws InstructionParseException {
        final Event event = factory == null ? null : factory.parse(instruction.copy());
        final StaticEvent staticEvent = staticFactory == null ? null : staticFactory.parseStatic(instruction.copy());
        return new QuestEventAdapter(instruction, event, staticEvent);
    }
}
