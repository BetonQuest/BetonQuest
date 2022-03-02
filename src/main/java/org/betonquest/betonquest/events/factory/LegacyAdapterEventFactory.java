package org.betonquest.betonquest.events.factory;

import lombok.CustomLog;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;

import java.lang.reflect.InvocationTargetException;

/**
 * Adapter to allow creation of {@link QuestEvent}s by {@link Class} instance.
 *
 * @param <T> type of the event
 */
@CustomLog
public class LegacyAdapterEventFactory<T extends QuestEvent> implements EventFactory {

    /**
     * Class of the event to create.
     */
    private final Class<T> eventClass;

    /**
     * Create a factory that can create events that conform to the old convention of how to define their constructor.
     *
     * @param eventClass event class to create with this factory
     */
    public LegacyAdapterEventFactory(final Class<T> eventClass) {
        this.eventClass = eventClass;
    }

    @Override
    public QuestEvent parseEventInstruction(final Instruction instruction) throws InstructionParseException {
        final Throwable error;
        try {
            return eventClass.getConstructor(Instruction.class).newInstance(instruction);
        } catch (final InvocationTargetException e) {
            final Throwable cause = e.getCause();
            if (cause instanceof InstructionParseException) {
                throw (InstructionParseException) cause;
            }
            error = e;
        } catch (final NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            error = e;
        }
        LOG.reportException(instruction.getPackage(), error);
        throw new InstructionParseException("A broken event prevents the creation of " + instruction, error);
    }
}
