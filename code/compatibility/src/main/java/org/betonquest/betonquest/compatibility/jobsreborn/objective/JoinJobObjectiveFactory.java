package org.betonquest.betonquest.compatibility.jobsreborn.objective;

import com.gamingmesh.jobs.api.JobsJoinEvent;
import com.gamingmesh.jobs.container.Job;
import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.api.quest.objective.event.ObjectiveFactoryService;
import org.betonquest.betonquest.compatibility.jobsreborn.JobParser;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

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
    public DefaultObjective parseInstruction(final Instruction instruction, final ObjectiveFactoryService service) throws QuestException {
        final Argument<Job> job = instruction.parse(JobParser.JOB).get();
        final JoinJobObjective objective = new JoinJobObjective(instruction, job);
        service.request(JobsJoinEvent.class).handler(objective::onJobsJoinEvent, this::fromEvent).subscribe(true);
        return objective;
    }

    @Nullable
    private Player fromEvent(final JobsJoinEvent event) {
        return event.getPlayer().getPlayer();
    }
}
