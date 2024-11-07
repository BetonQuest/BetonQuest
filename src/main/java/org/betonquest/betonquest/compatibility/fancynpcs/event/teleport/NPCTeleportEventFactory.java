package org.betonquest.betonquest.compatibility.fancynpcs.event.teleport;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.api.quest.event.StaticEvent;
import org.betonquest.betonquest.api.quest.event.StaticEventFactory;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEventAdapter;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadEvent;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadStaticEvent;
import org.betonquest.betonquest.utils.Utils;

/**
 * Factory to create {@link NPCTeleportEvent}s from {@link Instruction}s.
 */
public class NPCTeleportEventFactory implements EventFactory, StaticEventFactory {
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
    public Event parseEvent(final Instruction instruction) throws InstructionParseException {
        return new PrimaryServerThreadEvent(createNpcTeleportEvent(instruction), data);
    }

    @Override
    public StaticEvent parseStaticEvent(final Instruction instruction) throws InstructionParseException {
        return new PrimaryServerThreadStaticEvent(createNpcTeleportEvent(instruction), data);
    }

    private NullableEventAdapter createNpcTeleportEvent(final Instruction instruction) throws InstructionParseException {
        final String npcId = instruction.next();
        if (!Utils.isUUID(npcId)) {
            throw new InstructionParseException("NPC ID isn't a valid UUID");
        }
        final VariableLocation location = instruction.getLocation();
        return new NullableEventAdapter(new NPCTeleportEvent(npcId, location));
    }
}
