package org.betonquest.betonquest.quest.event.setblock;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.event.ComposedEvent;
import org.betonquest.betonquest.api.quest.event.ComposedEventFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadComposedEvent;
import org.betonquest.betonquest.utils.BlockSelector;

/**
 * Factory to create setblock events from {@link Instruction}s.
 */
public class SetBlockEventFactory implements ComposedEventFactory {
    /**
     * Data for primary server thread access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create the setblock event factory.
     *
     * @param data the data for primary server thread access
     */
    public SetBlockEventFactory(final PrimaryServerThreadData data) {
        this.data = data;
    }

    @Override
    public ComposedEvent parseComposedEvent(final Instruction instruction) throws InstructionParseException {
        final BlockSelector blockSelector = instruction.getBlockSelector(instruction.next());
        final VariableLocation variableLocation = instruction.getLocation();
        final boolean applyPhysics = !instruction.hasArgument("ignorePhysics");
        return new PrimaryServerThreadComposedEvent(
                new SetBlockEvent(blockSelector, variableLocation, applyPhysics),
                data
        );
    }
}
