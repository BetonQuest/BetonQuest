package org.betonquest.betonquest.compatibility.jobsreborn.event;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.Job;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadEvent;
import org.betonquest.betonquest.util.Utils;

/**
 * Factory to create {@link EventJoinJob}s from {@link Instruction}s.
 */
public class FactoryEventJoinJob implements EventFactory {
    /**
     * The data for the primary server thread.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create a new Factory to create Can Level Conditions.
     *
     * @param data the data for the primary server thread.
     */
    public FactoryEventJoinJob(final PrimaryServerThreadData data) {
        this.data = data;
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws QuestException {
        final String jobName = instruction.next();
        final Job job = Utils.getNN(Jobs.getJob(jobName), "Jobs Reborn job \"" + jobName + "\" does not exist");
        return new PrimaryServerThreadEvent(new EventJoinJob(job), data);
    }
}
