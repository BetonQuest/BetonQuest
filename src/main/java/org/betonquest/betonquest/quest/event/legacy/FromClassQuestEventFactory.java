package org.betonquest.betonquest.quest.event.legacy;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.BetonQuestLogger;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;

import java.lang.reflect.InvocationTargetException;

/**
 * Factory to create {@link QuestEvent}s with the old convention of the pre-defined constructor taking just one
 * {@link Instruction} argument.
 *
 * @param <T> type of the event
 * @deprecated new events must use an {@link EventFactory} instead
 */
@Deprecated
public class FromClassQuestEventFactory<T extends QuestEvent> implements QuestEventFactory {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private static final BetonQuestLogger LOG = BetonQuestLogger.create(FromClassQuestEventFactory.class);

    /**
     * Class of the event to create.
     */
    private final Class<T> eventClass;

    /**
     * Create the factory for a specific event class.
     *
     * @param eventClass event class to create with this factory
     */
    public FromClassQuestEventFactory(final Class<T> eventClass) {
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
