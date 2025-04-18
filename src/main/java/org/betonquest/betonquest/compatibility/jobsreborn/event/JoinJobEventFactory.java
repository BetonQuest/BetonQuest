package org.betonquest.betonquest.compatibility.jobsreborn.event;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.compatibility.jobsreborn.VariableJob;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadEvent;

/**
 * Factory to create {@link JoinJobEvent}s from {@link Instruction}s.
 */
public class JoinJobEventFactory implements PlayerEventFactory {
    /**
     * The data for the primary server thread.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create a new Factory to create Can Level Conditions.
     *
     * @param data the data for the primary server thread.
     */
    public JoinJobEventFactory(final PrimaryServerThreadData data) {
        this.data = data;
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        final VariableJob job = instruction.get(VariableJob::new);
        return new PrimaryServerThreadEvent(new JoinJobEvent(job), data);
    }
}
