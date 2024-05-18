package org.betonquest.betonquest.quest.event.point;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.event.HybridEvent;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.quest.event.StandardHybridEventFactory;
import org.betonquest.betonquest.utils.Utils;

/**
 * Factory to create delete points events from {@link Instruction}s.
 */
public class DeletePointEventFactory extends StandardHybridEventFactory {

    /**
     * Create the delete points event factory.
     */
    public DeletePointEventFactory() {
    }

    @Override
    public HybridEvent parseHybridEvent(final Instruction instruction) throws InstructionParseException {
        final String category = Utils.addPackage(instruction.getPackage(), instruction.next());
        return new DeletePointEvent(category);
    }
}
