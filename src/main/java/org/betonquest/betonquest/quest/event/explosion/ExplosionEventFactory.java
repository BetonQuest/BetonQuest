package org.betonquest.betonquest.quest.event.explosion;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.event.ComposedEvent;
import org.betonquest.betonquest.api.quest.event.ComposedEventFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadComposedEvent;

/**
 * Factory to create explosion events from {@link Instruction}s.
 */
public class ExplosionEventFactory implements ComposedEventFactory {
    /**
     * Data for primary server thread access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create the explosion event factory.
     *
     * @param data the data for primary server thread access
     */
    public ExplosionEventFactory(final PrimaryServerThreadData data) {
        this.data = data;
    }

    @Override
    public ComposedEvent parseComposedEvent(final Instruction instruction) throws InstructionParseException {
        final boolean setsFire = "1".equals(instruction.next());
        final boolean breaksBlocks = "1".equals(instruction.next());
        final VariableNumber power = instruction.getVarNum();
        final VariableLocation location = instruction.getLocation();
        return new PrimaryServerThreadComposedEvent(new ExplosionEvent(location, power, setsFire, breaksBlocks), data);
    }
}
