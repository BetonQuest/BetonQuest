package org.betonquest.betonquest.quest.event.objective;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.api.quest.event.StaticEvent;
import org.betonquest.betonquest.api.quest.event.StaticEventFactory;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEventAdapter;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.id.ObjectiveID;

import java.util.List;
import java.util.Locale;

/**
 * Factory for {@link ObjectiveEvent}s.
 */
public class ObjectiveEventFactory implements EventFactory, StaticEventFactory {

    /**
     * The BetonQuest instance.
     */
    private final BetonQuest betonQuest;

    /**
     * Logger factory to create a logger for events.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Creates a new factory for {@link ObjectiveEvent}s.
     *
     * @param betonQuest    the BetonQuest instance
     * @param loggerFactory the logger factory
     */
    public ObjectiveEventFactory(final BetonQuest betonQuest, final BetonQuestLoggerFactory loggerFactory) {
        this.betonQuest = betonQuest;
        this.loggerFactory = loggerFactory;
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws InstructionParseException {
        return createObjectiveEvent(instruction);
    }

    @Override
    public StaticEvent parseStaticEvent(final Instruction instruction) throws InstructionParseException {
        return createObjectiveEvent(instruction);
    }

    private NullableEventAdapter createObjectiveEvent(final Instruction instruction) throws InstructionParseException {
        final String action = instruction.next().toLowerCase(Locale.ROOT);
        final List<ObjectiveID> objectives = instruction.getList(instruction::getObjective);
        return new NullableEventAdapter(new ObjectiveEvent(betonQuest, loggerFactory.create(ObjectiveEvent.class), instruction.getPackage(), objectives, action));
    }

}
