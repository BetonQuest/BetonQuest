package org.betonquest.betonquest.quest.event.point;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.utils.Utils;

import java.util.Locale;

/**
 * Factory to create points events from {@link Instruction}s.
 */
public class PointEventFactory implements EventFactory {

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * Create the points event factory.
     */
    public PointEventFactory(final BetonQuestLogger log) {
        this.log = log;
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws InstructionParseException {
        final String categoryName = instruction.next();
        final String category = Utils.addPackage(instruction.getPackage(), categoryName);
        final String number = instruction.next();
        final boolean notify = instruction.hasArgument("notify");
        final String action = instruction.getOptional("action");
        final String fullId = instruction.getID().getFullID();
        if (action != null) {
            try {
                final Point type = Point.valueOf(action.toUpperCase(Locale.ROOT));
                return new PointEvent(log, categoryName, category, new VariableNumber(instruction.getPackage(), number), type, instruction.getPackage(), fullId, notify);
            } catch (final IllegalArgumentException e) {
                throw new InstructionParseException("Unknown modification action: " + action, e);
            }
        }
        if (!number.isEmpty() && number.charAt(0) == '*') {
            return new PointEvent(log, categoryName, category, new VariableNumber(instruction.getPackage(), number.replace("*", "")), Point.MULTIPLY, instruction.getPackage(), fullId, notify);
        }
        if (number.isEmpty() || number.charAt(0) == '-') {
            final String newNumber = number.replace("-", "");
            return new PointEvent(log, categoryName, category, new VariableNumber(instruction.getPackage(), newNumber), Point.SUBTRACT, instruction.getPackage(), fullId, notify);
        }
        return new PointEvent(log, categoryName, category, new VariableNumber(instruction.getPackage(), number), Point.ADD, instruction.getPackage(), fullId, notify);
    }
}
