package org.betonquest.betonquest.quest.event.point;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.quest.event.HybridEvent;
import org.betonquest.betonquest.api.quest.event.HybridEventFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.utils.Utils;

import java.util.Locale;

/**
 * Factory to create global points events from {@link Instruction}s.
 */
public class GlobalPointEventFactory implements HybridEventFactory {

    /**
     * Create the global points event factory.
     */
    public GlobalPointEventFactory() {
    }

    @Override
    public HybridEvent parseHybridEvent(final Instruction instruction) throws InstructionParseException {
        final String category = Utils.addPackage(instruction.getPackage(), instruction.next());
        final String number = instruction.next();
        final String action = instruction.getOptional("action");
        if (action != null) {
            try {
                final Point type = Point.valueOf(action.toUpperCase(Locale.ROOT));
                return new GlobalPointEvent(category, new VariableNumber(instruction.getPackage(), number), type);
            } catch (final IllegalArgumentException e) {
                throw new InstructionParseException("Unknown modification action: " + instruction.current(), e);
            }
        }
        if (!number.isEmpty() && number.charAt(0) == '*') {
            return new GlobalPointEvent(category, new VariableNumber(instruction.getPackage(), number.replace("*", "")), Point.MULTIPLY);
        }
        return new GlobalPointEvent(category, new VariableNumber(instruction.getPackage(), number), Point.ADD);
    }
}
