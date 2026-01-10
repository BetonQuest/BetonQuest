package org.betonquest.betonquest.compatibility.jobsreborn.objective;

import com.gamingmesh.jobs.api.JobsJoinEvent;
import com.gamingmesh.jobs.container.Job;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.objective.Objective;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.api.quest.objective.service.ObjectiveService;
import org.betonquest.betonquest.compatibility.jobsreborn.JobParser;

/**
 * Factory for creating {@link JoinJobObjective} instances from {@link Instruction}s.
 */
public class JoinJobObjectiveFactory implements ObjectiveFactory {

    /**
     * Creates a new instance of the ObjectiveJoinJobFactory.
     */
    public JoinJobObjectiveFactory() {
    }

    @Override
    public Objective parseInstruction(final Instruction instruction, final ObjectiveService service) throws QuestException {
        final Argument<Job> job = instruction.parse(JobParser.JOB).get();
        final JoinJobObjective objective = new JoinJobObjective(service, job);
        service.request(JobsJoinEvent.class).onlineHandler(objective::onJobsJoinEvent)
                .player(event -> event.getPlayer().getPlayer()).subscribe(true);
        return objective;
    }
}
