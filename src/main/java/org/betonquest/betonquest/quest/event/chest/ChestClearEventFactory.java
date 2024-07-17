package org.betonquest.betonquest.quest.event.chest;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.event.ComposedEvent;
import org.betonquest.betonquest.api.quest.event.ComposedEventFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadComposedEvent;

/**
 * Factory to create chest events from {@link Instruction}s.
 */
public class ChestClearEventFactory implements ComposedEventFactory {
    /**
     * Data for primary server thread access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create the chest clear event factory.
     *
     * @param data the data for primary server thread access
     */
    public ChestClearEventFactory(final PrimaryServerThreadData data) {
        this.data = data;
    }

    @Override
    public ComposedEvent parseComposedEvent(final Instruction instruction) throws InstructionParseException {
        final VariableLocation variableLocation = instruction.getLocation();
        return new PrimaryServerThreadComposedEvent(
                new ChestClearEvent(variableLocation), data);
    }
}
