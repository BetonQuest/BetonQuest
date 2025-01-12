package org.betonquest.betonquest.quest.event.point;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.api.quest.event.StaticEvent;
import org.betonquest.betonquest.api.quest.event.StaticEventFactory;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEventAdapter;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.quest.registry.processor.VariableProcessor;
import org.betonquest.betonquest.utils.Utils;

import java.util.Locale;

/**
 * Factory to create global points events from {@link Instruction}s.
 */
public class GlobalPointEventFactory implements EventFactory, StaticEventFactory {
    /**
     * Variable processor to create variables required by the event.
     */
    private final VariableProcessor variableProcessor;

    /**
     * Create the global points event factory.
     *
     * @param variableProcessor variable processor for creating variables
     */
    public GlobalPointEventFactory(final VariableProcessor variableProcessor) {
        this.variableProcessor = variableProcessor;
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws QuestException {
        return parseCombinedEvent(instruction);
    }

    @Override
    public StaticEvent parseStaticEvent(final Instruction instruction) throws QuestException {
        return parseCombinedEvent(instruction);
    }

    private NullableEventAdapter parseCombinedEvent(final Instruction instruction) throws QuestException {
        return new NullableEventAdapter(createGlobalPointEvent(instruction));
    }

    private GlobalPointEvent createGlobalPointEvent(final Instruction instruction) throws QuestException {
        final String category = Utils.addPackage(instruction.getPackage(), instruction.next());
        final String number = instruction.next();
        final String action = instruction.getOptional("action");
        if (action != null) {
            try {
                final Point type = Point.valueOf(action.toUpperCase(Locale.ROOT));
                return new GlobalPointEvent(category, new VariableNumber(variableProcessor, instruction.getPackage(), number), type);
            } catch (final IllegalArgumentException e) {
                throw new QuestException("Unknown modification action: " + instruction.current(), e);
            }
        }
        if (!number.isEmpty() && number.charAt(0) == '*') {
            return new GlobalPointEvent(category, new VariableNumber(variableProcessor, instruction.getPackage(), number.replace("*", "")), Point.MULTIPLY);
        }
        return new GlobalPointEvent(category, new VariableNumber(variableProcessor, instruction.getPackage(), number), Point.ADD);
    }
}
