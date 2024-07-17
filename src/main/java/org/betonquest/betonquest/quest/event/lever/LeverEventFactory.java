package org.betonquest.betonquest.quest.event.lever;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.event.ComposedEvent;
import org.betonquest.betonquest.api.quest.event.ComposedEventFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadComposedEvent;

/**
 * Factory for {@link LeverEvent}.
 */
public class LeverEventFactory implements ComposedEventFactory {
    /**
     * Data for primary server thread access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create a new LeverEventFactory.
     *
     * @param data the data for primary server thread access
     */
    public LeverEventFactory(final PrimaryServerThreadData data) {
        this.data = data;
    }

    @Override
    public ComposedEvent parseComposedEvent(final Instruction instruction) throws InstructionParseException {
        final VariableLocation location = instruction.getLocation();
        final StateType stateType = instruction.getEnum(StateType.class);
        return new PrimaryServerThreadComposedEvent(
                new LeverEvent(stateType, location),
                data
        );
    }
}
