package org.betonquest.betonquest.compatibility.citizens.event.teleport;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.event.ComposedEvent;
import org.betonquest.betonquest.api.quest.event.ComposedEventFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadComposedEvent;

/**
 * Factory to create {@link NPCTeleportEvent}s from {@link Instruction}s.
 */
public class NPCTeleportEventFactory implements ComposedEventFactory {
    /**
     * Data to use for syncing to the primary server thread.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create a new factory for NPC Teleport Events.
     *
     * @param data the data to use for syncing to the primary server thread
     */
    public NPCTeleportEventFactory(final PrimaryServerThreadData data) {
        this.data = data;
    }

    @Override
    public ComposedEvent parseComposedEvent(final Instruction instruction) throws InstructionParseException {
        final int npcId = instruction.getInt();
        if (npcId < 0) {
            throw new InstructionParseException("NPC ID cannot be less than 0");
        }
        final VariableLocation location = instruction.getLocation();
        return new PrimaryServerThreadComposedEvent(new NPCTeleportEvent(npcId, location), data);
    }
}
