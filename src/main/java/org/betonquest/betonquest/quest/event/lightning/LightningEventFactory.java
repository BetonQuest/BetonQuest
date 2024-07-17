package org.betonquest.betonquest.quest.event.lightning;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.event.ComposedEvent;
import org.betonquest.betonquest.api.quest.event.ComposedEventFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadComposedEvent;

/**
 * Factory for {@link LightningEvent} from the {@link Instruction}
 */
public class LightningEventFactory implements ComposedEventFactory {
    /**
     * Data for primary server thread access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create a new LightningEventFactory.
     *
     * @param data the data for primary server thread access
     */
    public LightningEventFactory(final PrimaryServerThreadData data) {
        this.data = data;
    }

    @Override
    public ComposedEvent parseComposedEvent(final Instruction instruction) throws InstructionParseException {
        final VariableLocation location = instruction.getLocation();
        final boolean noDamage = instruction.hasArgument("noDamage");
        return new PrimaryServerThreadComposedEvent(
                new LightningEvent(location, noDamage),
                data
        );
    }
}
