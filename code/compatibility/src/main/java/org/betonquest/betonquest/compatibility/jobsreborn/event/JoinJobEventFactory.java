package org.betonquest.betonquest.compatibility.jobsreborn.event;

import com.gamingmesh.jobs.container.Job;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.compatibility.jobsreborn.JobParser;

/**
 * Factory to create {@link JoinJobEvent}s from {@link Instruction}s.
 */
public class JoinJobEventFactory implements PlayerEventFactory {

    /**
     * Create a new Factory to create Can Level Conditions.
     */
    public JoinJobEventFactory() {
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<Job> job = instruction.parse(JobParser.JOB).get();
        return new JoinJobEvent(job);
    }
}
