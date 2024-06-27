package org.betonquest.betonquest.quest.event.random;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.event.ComposedEvent;
import org.betonquest.betonquest.api.quest.event.ComposedEventFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.instruction.variable.VariableNumber;

import java.util.List;

/**
 * Creates new {@link PickRandomEvent} instances from an {@link Instruction}.
 */
public class PickRandomEventFactory implements ComposedEventFactory {
    /**
     * The percentage character.
     */
    private static final char PERCENTAGE = '%';

    /**
     * The number of minimum percentages.
     */
    private static final int NUMBER_OF_MINIMUM_PERCENTAGES = 1;

    /**
     * The number of maximum percentages.
     */
    private static final int NUMBER_OF_MAXIMUM_PERCENTAGES = 3;

    /**
     * Creates the PickRandomEventFactory.
     */
    public PickRandomEventFactory() {
    }

    @Override
    @SuppressWarnings("PMD.CognitiveComplexity")
    public ComposedEvent parseComposedEvent(final Instruction instruction) throws InstructionParseException {
        final List<RandomEvent> events = instruction.getList(string -> {
            if (string == null) {
                return null;
            }
            if (!string.matches("(\\d+\\.?\\d?|%.*%)%.+")) {
                throw new InstructionParseException("Percentage must be specified correctly: " + string);
            }

            int index = 0;
            int count = 0;
            while (index < string.length()) {
                if (string.charAt(index) == PERCENTAGE) {
                    count++;
                }
                index++;
            }

            final String[] parts = string.split(String.valueOf(PERCENTAGE));
            final EventID eventID;

            if (NUMBER_OF_MINIMUM_PERCENTAGES == count) {
                try {
                    eventID = new EventID(instruction.getPackage(), parts[1]);
                } catch (final ObjectNotFoundException e) {
                    throw new InstructionParseException("Error while loading event: " + e.getMessage(), e);
                }
                final VariableNumber chance = new VariableNumber(instruction.getPackage(), parts[0]);
                return new RandomEvent(eventID, chance);
            } else if (NUMBER_OF_MAXIMUM_PERCENTAGES == count) {
                try {
                    eventID = new EventID(instruction.getPackage(), parts[3]);
                } catch (final ObjectNotFoundException e) {
                    throw new InstructionParseException("Error while loading event: " + e.getMessage(), e);
                }
                final VariableNumber chance = new VariableNumber(instruction.getPackage(), "%" + parts[1] + "%");
                return new RandomEvent(eventID, chance);
            }
            throw new InstructionParseException("Error while loading event: '" + instruction.getEvent().getFullID() + "'. Wrong number of % detected. Check your event.");
        });
        final VariableNumber amount = instruction.getVarNum(instruction.getOptional("amount"));
        return new PickRandomEvent(events, amount);
    }
}
