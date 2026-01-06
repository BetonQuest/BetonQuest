package org.betonquest.betonquest.compatibility.jobsreborn.event;

import com.gamingmesh.jobs.container.Job;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.compatibility.jobsreborn.JobParser;

/**
 * Factory to create {@link SetLevelEvent}s from {@link Instruction}s.
 */
public class SetLevelEventFactory implements PlayerActionFactory {

    /**
     * Create a new Factory to create Can Level Conditions.
     */
    public SetLevelEventFactory() {
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<Job> job = instruction.parse(JobParser.JOB).get();
        final Argument<Number> amount = instruction.number().get();
        return new SetLevelEvent(job, amount);
    }
}
