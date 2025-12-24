package org.betonquest.betonquest.compatibility.jobsreborn.event;

import com.gamingmesh.jobs.container.Job;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.compatibility.jobsreborn.JobParser;

/**
 * Factory to create {@link AddLevelEvent}s from {@link Instruction}s.
 */
public class AddLevelEventFactory implements PlayerEventFactory {

    /**
     * Create a new Factory to create Can Level Conditions.
     */
    public AddLevelEventFactory() {
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        final Variable<Job> job = instruction.parse(JobParser.JOB).get();
        final Variable<Number> amount = instruction.number().get();
        return new AddLevelEvent(job, amount);
    }
}
